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

public class FileInfo {

    private boolean indexed;
    private Path root;
    private Path path;
    private Date dateOff;
    private Collection<String> tags = new HashSet<String>();
    private UUID uuid = UUID.randomUUID();

    public Path getPath() {
        return path;
    }
    
    public String getName() {
        return path.getFileName().toString();
    }
    
    public static FileInfo fromMap(Map m) {
        FileInfo fi = new FileInfo();
        String path = (String)m.get("path");
        String root = (String)m.get("root");
        Long dateOff = (Long)m.get("dateOff");
        String uuid = (String)m.get("uuid");
        Boolean indexed = (Boolean)m.get("indexed");
        Collection tags = (Collection)m.get("tags");
        if (path != null) {
            fi.path = Paths.get(path);
        }
        if (root != null) {
            fi.root = Paths.get(root);
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
        return fi;
    }
    public Map<String, Object> getProperties() {
        Map ret = new LinkedHashMap();
        if (path != null) {
            ret.put("path", path.toString());
        }
        if (root != null) {
            ret.put("root", path.toString());
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

    public FileInfo setPath(Path path, Path root) {
        this.path = path;
        this.root = root;
        return this;
    }

    public FileInfo setFile(File file) {
        path = Paths.get(file.getAbsolutePath());
        return this;
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
    public static long calculatePathSize(Path path) { //TODO
        try {
            if (Files.isDirectory(path)) {
                return Files.list(path).mapToLong(FileInfo::calculatePathSize).sum();
            } else
            if (Files.isRegularFile(path)) {
                return Files.size(path);
            }
        } catch (IOException e) {
        }
        return 0L;
    }
    
}
