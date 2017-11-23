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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import ru.org.sevn.common.solr.SolrIndexer;

//keep
//keeptill
//fix
//change
//
//fix
//tmp
//change
public class StoreDistribute implements Runnable {
    public static final String DIR_INDIR = "in";
    public static final String DIR_ERROR = "error";
    public static final String DIR_OUT = "out";
    public static final String DIR_ERROR_OTHER = "other";
    public static final String DIR_ERROR_EXISTS = "exists";
    public static final String DIR_TMP = "tmp";
    public static final String DIR_KEEP = "keep";
    public static final String DIR_KEEP_TILL = "keeptill";
    public static final String DIR_FIX = "fix";
    public static final String DIR_CHANGE = "change";
    
    private File storeDir;
    
    private File inDir;
    private File errorDir;
    
    private File keepDir;
    private File keepTill;
    private File fix;
    private File change;
    
    private File storageDir;
    
    private Map<String, FixStoreFileManager> fixStoreFileManagerMap = new HashMap<>();
    private TempStoreFileManager tempStoreFileManager;
    private ChangeStoreFileManager changeStoreFileManager;
    
    public StoreDistribute(File storeDir, SolrIndexer indexer, String[] fixStores) {
        this.storeDir = storeDir;
        inDir = mkDir(storeDir, DIR_INDIR);
        errorDir = mkDir(storeDir, DIR_ERROR);
        storageDir = mkDir(storeDir, DIR_OUT);
        
        keepDir = mkDir(inDir, DIR_KEEP);
        keepTill = mkDir(inDir, DIR_KEEP_TILL);
        fix = mkDir(inDir, DIR_FIX);
        change = mkDir(inDir, DIR_CHANGE);
        
        for (String s : fixStores) {
            s = "##" + s;
            fixStoreFileManagerMap.put(s, new FixStoreFileManager(mkDir(storageDir, s), indexer));
        }
        fixStoreFileManagerMap.put(DIR_FIX, new FixStoreFileManager(mkDir(storageDir, DIR_FIX), indexer));
        tempStoreFileManager = new TempStoreFileManager(mkDir(storageDir, DIR_TMP), indexer);
        changeStoreFileManager = new ChangeStoreFileManager(mkDir(storageDir, DIR_CHANGE), indexer);
    }
    
    public void restoreIndexing() {
        changeStoreFileManager.restoreIndexing();
        tempStoreFileManager.restoreIndexing();
        for (String k : fixStoreFileManagerMap.keySet()) {
            fixStoreFileManagerMap.get(k).restoreIndexing();
        }
    }
    //media
    //video
    //audio
    //pictures
    //books
    //personal
    //other - fix
    
    private File mkDir(File parent, String name) {
        File ret = new File(parent, name);
        if (!ret.exists()) {
            ret.mkdirs();
        }
        return ret;
    }
    
    private final SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
    private final Pattern keeptillP = Pattern.compile("\\d{8}");
    private final Pattern keepP = Pattern.compile("[ymdw]\\d{1,4}");
    private final Pattern weekP = Pattern.compile("w\\d{1,3}");
    private final Pattern yearP = Pattern.compile("y\\d{4}");
    private final Pattern monthP = Pattern.compile("m\\d{1,2}");
    private final Pattern dayP = Pattern.compile("d\\d{1,2}");
    
    enum PersistType {
        FIX, TEMP, CHANGE
    }
    
    private StoreFileManager getStoreFileManager(PersistType pt, FileInfo fi) {
            switch(pt) {
                case FIX:
                    for (String ks : fixStoreFileManagerMap.keySet()) {
                        if (fi.getTags().contains(ks)) {
                            return fixStoreFileManagerMap.get(ks);
                        }
                    }
                    return fixStoreFileManagerMap.get(DIR_FIX);
                case TEMP:
                    return tempStoreFileManager;
                case CHANGE:
                    return changeStoreFileManager;
            }
        return null; //TODO
    }
    // /#tag1/#tag2/#tag3/
    private void processFile(File basedir, PersistType pt, File file, HashSet<String> tags, KeepInfo keepInfo) {
        System.out.println("processFile>"+file.getAbsolutePath());
        if (file.isDirectory() && file.getName().startsWith("#")) {
            for (File f : file.listFiles()) {
                HashSet<String> t = new HashSet<>(tags);
                t.add(file.getName());
                processFile(basedir, pt, f, t, keepInfo);
            }
        } else {
            FileInfo fi = new FileInfo().setFile(file);
            fi.getTags().addAll(tags);
            
            if (keepInfo != null) {
                Date dayOff = keepInfo.dateOff;
                if (dayOff == null) {
                    dayOff = getDateOff(file, keepInfo);
                }
                fi.setDateOff(dayOff);
            }
            
            StoreFileManager.Errors err = getStoreFileManager(pt, fi).addFile(fi);
            if (err != null) {
                File file2dir = mkDir(this.errorDir, err.name());
                Path file2dirPath = Paths.get(file2dir.getAbsolutePath());
                Path filePath = Paths.get(file.getAbsolutePath());
                Path fileBasePath = Paths.get(basedir.getAbsolutePath());
                
                Path filePathRel = fileBasePath.relativize(filePath);
                
                File file2 = file2dirPath.resolve(filePathRel).toFile();
                file2.getParentFile().mkdirs();
                file.renameTo(file2);
            }
        }
    }
    
    private void processKeep(File f, KeepInfo keepInfo) {
        processFile(keepInfo.basedir, PersistType.TEMP, f, new HashSet<String>(), keepInfo);
    }
    
    private void processKeep(File basedir, File f, Date dateOff) {
        KeepInfo ki = new KeepInfo(basedir);
        ki.dateOff = dateOff;
        processKeep(f, ki);
    }
    
    @Override
    public void run() {
        processFix();
        processChange();
        processKeep();
        processKeepTill();
    }
    private void processFix() {
        System.out.println("processFile>"+fix.getAbsolutePath());
        for (File f : fix.listFiles()) {
            processFile(this.fix, PersistType.FIX, f, new HashSet<String>(), null);
        }
    }
    private void processChange() {
        for (File f : change.listFiles()) {
            processFile(this.change, PersistType.CHANGE, f, new HashSet<String>(), null);
        }
    }
    private void processKeepTill() {
        for (File dir : keepTill.listFiles()) {
            if (dir.isDirectory() && keeptillP.matcher(dir.getName()).matches()) {
                try {
                    Date dateOff = yyyymmdd.parse(dir.getName());
                    for (File f : dir.listFiles()) {
                        processKeep(this.keepTill, f, dateOff);
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(StoreDistribute.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private void processKeep() {
        for (File f : keepDir.listFiles()) {
            if (f.isDirectory() && keepP.matcher(f.getName()).matches()) {
                KeepInfo keepInfo =new KeepInfo(keepDir);
                if (dayP.matcher(f.getName()).matches()) {
                    processKeepDay(keepInfo, f);
                } else if (weekP.matcher(f.getName()).matches()) {
                    processKeepWeek(keepInfo, f);
                } else if (monthP.matcher(f.getName()).matches()) {
                    processKeepMonth(keepInfo, f);
                } else if (yearP.matcher(f.getName()).matches()) {
                    processKeepYear(keepInfo, f);
                }
            }
        }
    }
    private Date getDateOff(File f, KeepInfo keepInfo) {
        long crDate = f.lastModified();
        try {
            BasicFileAttributes attr = Files.readAttributes(Paths.get(f.getAbsolutePath()), BasicFileAttributes.class);
            crDate = Math.min(attr.creationTime().toMillis(), crDate);
        } catch (IOException ex) {
            Logger.getLogger(StoreDistribute.class.getName()).log(Level.SEVERE, null, ex);
        }
        Calendar dateOff = Calendar.getInstance();
        dateOff.setTimeInMillis(crDate);
        dateOff.add(Calendar.WEEK_OF_YEAR, keepInfo.w);
        dateOff.add(Calendar.YEAR, keepInfo.y);
        dateOff.add(Calendar.MONTH, keepInfo.m);
        dateOff.add(Calendar.DATE, keepInfo.d);
        return dateOff.getTime();
    }
    private void processKeepDay(KeepInfo keepInfo, File dir) {
        int i = Integer.parseInt(dir.getName().substring(1));
        keepInfo.d += i;
        for (File f : dir.listFiles()) {
            processKeep(f, keepInfo);
        }
    }
    private void processKeepWeek(KeepInfo keepInfo, File dir) {
        int i = Integer.parseInt(dir.getName().substring(1));
        keepInfo.w += i;
        for (File f : dir.listFiles()) {
            if (dayP.matcher(f.getName()).matches()) {
                processKeepDay(keepInfo, f);
            } else {
                processKeep(f, keepInfo);
            }
        }
    }
    private void processKeepMonth(KeepInfo keepInfo, File dir) {
        int i = Integer.parseInt(dir.getName().substring(1));
        keepInfo.m += i;
        for (File f : dir.listFiles()) {
            if (dayP.matcher(f.getName()).matches()) {
                processKeepDay(keepInfo, f);
            } else if (weekP.matcher(f.getName()).matches()) {
                processKeepWeek(keepInfo, f);
            } else {
                processKeep(f, keepInfo);
            }
        }
    }
    private void processKeepYear(KeepInfo keepInfo, File dir) {
        int i = Integer.parseInt(dir.getName().substring(1));
        keepInfo.y += i;
        for (File f : dir.listFiles()) {
            if (dayP.matcher(f.getName()).matches()) {
                processKeepDay(keepInfo, f);
            } else if (weekP.matcher(f.getName()).matches()) {
                processKeepWeek(keepInfo, f);
            } else if (monthP.matcher(f.getName()).matches()) {
                processKeepMonth(keepInfo, f);
            } else {
                processKeep(f, keepInfo);
            }
        }
    }
    public static class KeepInfo {
        private File basedir;
        private int w;
        private int y;
        private int m;
        private int d;
        private Date dateOff;
        public KeepInfo(File basedir) {
            this.basedir = basedir;
        }
    }
}
