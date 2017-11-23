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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class PartInfo {
    
    private Date backUpDate;
    private long size;
    private int num;
    private ArrayList<FileInfo> files2index = new ArrayList<>(); //TODO use set

    private final java.io.File root;
    
    public PartInfo(java.io.File root) {
        this.root = root;
    }
    
    public Map getProperties() {
        LinkedHashMap ret = new LinkedHashMap();
        if (backUpDate != null) {
            ret.put("backUpDate", backUpDate.getTime());
        }
        ret.put("size", size);
        ret.put("num", num);
        
        ret.put("files2index", files2index.stream().map(e -> e.getProperties()).collect(Collectors.toList()));
        return ret;
    }

    public static PartInfo fromMap(Map m, java.io.File root) {
        PartInfo pi = new PartInfo(root);
        if (m.containsKey("size")) {
            pi.size = Long.parseLong(m.get("size").toString());
        }
        if (m.containsKey("num")) {
            pi.num = Integer.parseInt(m.get("num").toString());
        }
        if (m.containsKey("backUpDate")) {
            pi.backUpDate = new Date(Long.parseLong(m.get("backUpDate").toString()));
        }
        if (m.containsKey("files2index")) {
            for(Object e : ((Collection)m.get("files2index"))) {
                pi.files2index.add(FileInfo.fromMap((Map)e, root.toPath()));
            }
        }
        return pi;
    }

    public Date getBackUpDate() {
        return backUpDate;
    }

    public void setBackUpDate(Date backUpDate) {
        this.backUpDate = backUpDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void incrSize(long size) {
        this.size += size;
    }
    
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    
    public void addFileInfo(FileInfo s) {
        files2index.add(s);
        s.setRoot(root.toPath());
    }
    
    public void removeFileInfo(FileInfo s) {
        s.setRoot(null);
        files2index.remove(s);
    }
    
    public ArrayList<FileInfo> getFileInfoList() {
        ArrayList<FileInfo> ret = new ArrayList<>(files2index);
        return ret;
    }
    
    public JSONObject getJSONObject() /*throws Exception*/ {
        JSONObject ret = new JSONObject();
        Map<String, Object> props = getProperties();
        for (String k : props.keySet()) {
            ret.put(k, props.get(k));
        }
        return ret;
    }}
