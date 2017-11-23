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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;

public class FileInfo {

    private boolean indexed;
    private Path root;
    private Path path;
    private Date dateOff;
    private Collection<String> tags = new HashSet<String>();
    private UUID uuid = UUID.randomUUID();

    public FileInfo() {
        
    } 
    
    public boolean equals(Object obj) {
        boolean ret = super.equals(obj);
        if (!ret && obj instanceof FileInfo) {
            FileInfo fi = (FileInfo)obj;
            if (fi.uuid != null) {
                ret = uuid.equals(fi.uuid);
            }
        }
        return ret;
    }
    
    public Path getPath() {
        return path;
    }
    
    public String getName() {
        return path.getFileName().toString();
    }
    
    public static FileInfo fromMap(Map m, Path root) {
        return fromMap(m, new FileInfo(), root);
    }
    public static FileInfo fromMap(Map m, FileInfo fi, Path root) {
        //TODO read relative make absolute
        String path = (String)m.get("path");
        Long dateOff = (Long)m.get("dateOff");
        String uuid = (String)m.get("uuid");
        Boolean indexed = (Boolean)m.get("indexed");
        Collection tags = (Collection)m.get("tags");
        if (path != null) {
            fi.path = Paths.get(path);
            if (!fi.path.isAbsolute()) {
                if (root != null) {
                    fi.path = root.resolve(fi.path);
                }
            }
        }
        if (dateOff != null) {
            fi.dateOff = new Date(dateOff);
        }
        if (uuid != null) {
            fi.uuid = UUID.fromString(uuid);
        }
        if (tags != null) {
            fi.tags.addAll(tags);
        }
        if (indexed != null) {
            fi.indexed = indexed;
        }
        fi.setRoot(root);
        return fi;
    }
    public Map<String, Object> getProperties() {
        Map ret = new LinkedHashMap();
        if (path != null) {
            if (root != null && path.isAbsolute()) {
                ret.put("path", root.toAbsolutePath().relativize(path).toString());
            } else {
                ret.put("path", path.toString());
            }
        }
        if (dateOff != null) {
            ret.put("dateOff", dateOff.getTime());
        }
        if (uuid != null) {
            ret.put("uuid", uuid.toString());
        }
        if (tags != null) {
            ret.put("tags", new ArrayList(tags));
        }
        ret.put("indexed", indexed);
        return ret;
    }

    public FileInfo setPath(Path path) {
        this.path = path;
        return this;
    }

    public FileInfo setFile(File file) {
        return setPath(Paths.get(file.getAbsolutePath()));
    }

    public Date getDateOff() {
        return dateOff;
    }

    public FileInfo setDateOff(Date dateOff) {
        this.dateOff = dateOff;
        return this;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }
    
    
    private long fileSize = -1;
    public long getFileSize() {
        if (fileSize < 0) {
            fileSize = calculatePathSize(path);
        }
        return fileSize;
    }
    public long getFileSizeCalculated() {
        fileSize = calculatePathSize(path);
        return fileSize;
    }
    public static long calculatePathSize(Path path) { //TODO
        try {
            if (Files.isDirectory(path)) {
                return Files.list(path).mapToLong(FileInfo::calculatePathSize).sum();
            } else
            if (Files.isRegularFile(path)) {
                return Files.size(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }
    public JSONObject getJSONObject() /*throws Exception*/ {
        JSONObject ret = new JSONObject();
        Map<String, Object> props = getProperties();
        for (String k : props.keySet()) {
            ret.put(k, props.get(k));
        }
        return ret;
    }
    public void fromJSONObject(JSONObject jo) /*throws Exception*/ {
        Map ret = new LinkedHashMap();
        for (String k : jo.keySet()) {
            ret.put(k, jo.get(k));
        }
        fromMap(ret, this, this.root);
    }

    public void setRoot(Path root) {
        this.root = root;
    }
}
