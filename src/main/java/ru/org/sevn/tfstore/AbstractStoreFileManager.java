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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    public static String PART_DATA = "data";
    // dir/dir1/data
    // dir/dir1/.info
    // dir/.info
    private final File dir;
    private int lastnum = 0;
    private HashMap<Integer, PartInfo> parts = new HashMap();
    private SolrIndexer indexer;

    private synchronized void storeFileInfo(FileInfo file) {
        //TODO store
    }
    
    
    public AbstractStoreFileManager(File dir) {
        this.dir = dir;
        for (File f : dir.listFiles()) {
            if (f.isDirectory() && nameP.matcher(f.getName()).matches()) {
                PartInfo pi = readPartInfo(f);
                lastnum = Math.max(lastnum, pi.num);
                if (pi.backUpDate == null) {
                    parts.put(pi.num, pi);
                }
            }
        }

    }
    
    protected synchronized PartInfo getPartInfoFor(FileInfo fi) {
        for(PartInfo pi : parts.values()) {
            if (pi.size + fi.getFileSize() < MAX_SIZE) {
                return pi;
            }
        }
        PartInfo npi = new PartInfo();
        npi.num = this.lastnum + 1;
        this.lastnum = npi.num;
        parts.put(npi.num, npi);
        File npiFile = getPartDataDir(npi.num);
        npiFile.mkdirs();
        return npi;
    }
    
    protected PartInfo readPartInfo(File dir) {
        PartInfo ret = new PartInfo();
        
        try {
            File f = new File(dir, PART_INFO);
            int num = Integer.parseInt(f.getName().substring(1));
            Path fpath = Paths.get(f.getAbsolutePath());
            if (f.exists()) {
                String fstr = new String(Files.readAllBytes(fpath), "UTF-8");
                ret = PartInfo.fromMap((Map)ObjectBuilder.fromJSON(fstr));
            }
            ret.num = num;
            ret.size = FileInfo.calculatePathSize(fpath);
        } catch (Exception ex) {
            Logger.getLogger(AbstractStoreFileManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return ret;
    }
    
    protected File getPartDataDir(int num) {
        return new File(new File(dir, PART_NAME + num), PART_DATA);
    }
    
    protected Path makeRelativePath(FileInfo file) {
        Path fileRelPath = getPathFromUUID(file.getUuid().toString());
        fileRelPath = fileRelPath.resolve(file.getName());
        return fileRelPath;
    }
    
    @Override
    public Errors addFileIn(FileInfo file) {
        PartInfo pi = getPartInfoFor(file);
        pi.size += file.getFileSize();
        
        Path toDirPath = Paths.get(getPartDataDir(pi.num).getAbsolutePath());
        Path toFile = toDirPath.resolve(makeRelativePath(file));
        if (file.getPath().toFile().renameTo(toFile.toFile())) {
            file.setPath(toFile, toDirPath);
            storeFileInfo(file);
            //(String wpath, String path, File fl, String title, Consumer<Throwable> result) {

            HashMap<String, Object> tags = new HashMap();
            tags.put("tags_ss", file.getTags().toArray());
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
                                    file.setIndexed(true);
                                    storeFileInfo(file);
                                }
                            }
                        });
                    }
                }
            });
        }
        return null;
    }
    
    protected abstract String getStoreIdName(); 
    
    //TODO move to util
    public static String getRelative(File dir, File file) {
        Path dirPath = Paths.get(dir.getAbsolutePath());
        Path filePath = Paths.get(dir.getAbsolutePath());
        return dirPath.relativize(filePath).toString();
    }
    
    public synchronized void backUp(File partFile) throws Exception {
        if (nameP.matcher(partFile.getName()).matches()) {
            int num = Integer.parseInt(partFile.getName().substring(1));
            PartInfo pi = parts.remove(num);
            if (pi != null) {
                pi.setBackUpDate(new Date());
                writePartInfo(pi);
            }
        }
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
        File pfile = new File(new File(dir, PART_NAME + pi.num), PART_INFO);
        Path path = Paths.get(pfile.getAbsolutePath());
        byte[] bytes;
        bytes = JSONUtil.toJSON(pi.getProperties()).getBytes("UTF-8");
        Files.write(path, bytes);
    }

    public int getLastnum() {
        return lastnum;
    }

    protected void setLastnum(int lastnum) {
        this.lastnum = lastnum;
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
}
