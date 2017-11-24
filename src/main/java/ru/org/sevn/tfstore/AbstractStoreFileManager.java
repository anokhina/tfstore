/*
 * Copyright 2017 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.org.sevn.tfstore;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.noggit.JSONUtil;
import org.noggit.ObjectBuilder;
import ru.org.sevn.common.solr.SolrIndexer;

public abstract class AbstractStoreFileManager implements StoreFileManager {

    protected final Pattern nameP = Pattern.compile("p\\d{1,2}");
    
    public static String PART_NAME = "p";
    public static String PART_INFO = ".info";
    // dir/dir1/
    // dir/dir1.info
    private final File dir;
    private AtomicInteger lastnum = new AtomicInteger(0);
    private HashMap<Integer, PartInfo> parts = new HashMap();
    private SolrIndexer indexer;

    private synchronized void storeFileInfo(PartInfo pi, FileInfo file) {
        if (file.isIndexed()) {
            pi.removeFileInfo(file);
        } else {
            pi.addFileInfo(file);
        }
        try {
            writePartInfo(pi);
        } catch (Exception ex) {
            //TODO FATAL
            Logger.getLogger(AbstractStoreFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public AbstractStoreFileManager(File dir, SolrIndexer indexer) {
        this.indexer = indexer;
        this.dir = dir;
        restoreIndexing();
    }
    
    public synchronized void restoreIndexing() {
        for (File f : dir.listFiles()) { 
            if (f.isDirectory() && nameP.matcher(f.getName()).matches()) {
                PartInfo pi = readPartInfo(f);
                lastnum.set(Math.max(lastnum.get(), pi.getNum()));
                if (pi.getBackUpDate() == null) { //NO BACK UP
                    parts.put(pi.getNum(), pi);
                    //restore index queue
                    System.out.println("restore index queue>>>"+lastnum.get() + ":" + dir.getAbsolutePath()+":"+pi.getJSONObject().toString(2));
                    for (FileInfo fi : pi.getFileInfoList()) { //TODO it's empty
                        index(fi, pi);
                    }
                }
            }
        }
    }
    
    protected synchronized PartInfo selectPartInfoFor(FileInfo fi) {
        for(PartInfo pi : parts.values()) {
            if (pi.getSize() + fi.getFileSize() < MAX_SIZE1) {
                return pi;
            }
        }
        int lnum = this.lastnum.incrementAndGet();
        File npiFile = getPartDataDir(lnum);
        npiFile.mkdirs();
        
        PartInfo npi = new PartInfo(npiFile);
        npi.setNum(lnum);
        parts.put(npi.getNum(), npi);
        return npi;
    }
    
    protected PartInfo readPartInfo(File dir) {
        PartInfo ret = new PartInfo(dir);
        
        try {
            File f = new File(dir.getParent(), dir.getName() + PART_INFO);
            int num = Integer.parseInt(dir.getName().substring(1));
            Path fpath = Paths.get(f.getAbsolutePath());
            if (f.exists()) {
                String fstr = new String(Files.readAllBytes(fpath), "UTF-8");
                ret = PartInfo.fromMap((Map)ObjectBuilder.fromJSON(fstr), dir);
            }
            ret.setNum(num);
            ret.setSize(FileInfo.calculatePathSize(dir.toPath()));
        } catch (Exception ex) {
            Logger.getLogger(AbstractStoreFileManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return ret;
    }
    
    protected File getPartDataDir(int num) {
        return new File(dir, PART_NAME + num);
    }
    
    protected Path makeRelativePath(FileInfo file) {
        Path fileRelPath = getPathFromUUID(file.getUuid().toString());
        fileRelPath = fileRelPath.resolve(file.getName());
        return fileRelPath;
    }
    
    @Override
    public Errors addFileIn(FileInfo file) {
        PartInfo pi = selectPartInfoFor(file);
        pi.incrSize(file.getFileSize());
        
        Path toDirPath = Paths.get(getPartDataDir(pi.getNum()).getAbsolutePath());
        Path toFile = toDirPath.resolve(makeRelativePath(file));
        File toFileFile = toFile.toFile();
        toFileFile.getParentFile().mkdirs();
        if (file.getPath().toFile().renameTo(toFileFile)) {
            file.setFile(toFileFile);
            storeFileInfo(pi, file);
            //(String wpath, String path, File fl, String title, Consumer<Throwable> result) {

            index(file, pi);
        } else {
            // TODO error
        }
        return null;
    }
    
    private void index(FileInfo file, PartInfo pi) {
        HashMap<String, Object> tags = new HashMap();
        tags.put("tags_ss", new ArrayList<String>(file.getTags()));
        indexer.addDocAsync(
                getStoreIdName(), 
                getRelative(dir, file.getPath().toFile()), 
                file.getPath().toFile(), 
                file.getName(), 
                tags,
                new Consumer<Throwable>() {

            @Override
            public void accept(Throwable t) {
                if (t == null) {

                    indexer.commitAsync(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable t) {
                            if (t == null) {
                                System.out.println("INDEXED>"+file.getPath());
                                file.setIndexed(true);
                                //TODO store in db
                                storeFileInfo(pi, file);
                            }
                        }
                    });
                }
            }
        });
    }
    
    protected abstract String getStoreIdName(); 
    
    //TODO move to util
    public static String getRelative(File dir, File file) {
        Path dirPath = Paths.get(dir.getAbsolutePath());
        Path filePath = Paths.get(file.getAbsolutePath());
        return dirPath.relativize(filePath).toString();
    }
    
    public synchronized boolean backUp(File partFile) throws Exception {
        if (nameP.matcher(partFile.getName()).matches()) {
            int num = Integer.parseInt(partFile.getName().substring(1));
            PartInfo pi = parts.remove(num);
            if (pi != null) {
                pi.setBackUpDate(new Date());
                writePartInfo(pi);
                System.out.println("back up>" + partFile.getAbsolutePath());
                return true;
            }
        }
        return false;
    }
    
    // TODO move to util
    public static Path getPathFromUUID(String id) {
        //b177f63a-fce9-48a3-92b8-3124d0c01868
        //b17 7f63 afce 948 a392b83124d0c01868
        String sid = id.replace("-", "");
        int d = 3;
        Path ret = Paths.get(sid.substring(0, d));
        int i = 0;
        for (i = 1; i < 3; i++) {
            ret = ret.resolve(sid.substring(i*d, i*d+d));
        }
        ret = ret.resolve(sid.substring(i*d));
        return ret;
    }
    
    protected void writePartInfo(PartInfo pi) throws Exception {
        File pfile = new File(dir, PART_NAME + pi.getNum() + PART_INFO);
        Path path = Paths.get(pfile.getAbsolutePath());
        byte[] bytes;
        bytes = JSONUtil.toJSON(pi.getProperties()).getBytes("UTF-8");
        Files.write(path, bytes);
    }

    public int getLastnum() {
        return lastnum.get();
    }

    public HashMap<Integer, PartInfo> getParts() {
        return parts;
    }

    public SolrIndexer getIndexer() {
        return indexer;
    }

    public void setIndexer(SolrIndexer indexer) {
        this.indexer = indexer;
    }

    public File getDir() {
        return dir;
    }
}
