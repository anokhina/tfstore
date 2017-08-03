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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class PartInfo {
    
    Date backUpDate;
    long size;
    int num;

    public Map getProperties() {
        LinkedHashMap ret = new LinkedHashMap();
        if (backUpDate != null) {
            ret.put("backUpDate", backUpDate.getTime());
        }
        ret.put("size", size);
        ret.put("num", num);
        return ret;
    }

    public static PartInfo fromMap(Map m) {
        PartInfo pi = new PartInfo();
        if (m.containsKey("size")) {
            pi.size = Long.parseLong((String) m.get("size"));
        }
        if (m.containsKey("num")) {
            pi.num = Integer.parseInt((String) m.get("num"));
        }
        if (m.containsKey("backUpDate")) {
            pi.backUpDate = new Date(Long.parseLong((String) m.get("backUpDate")));
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    
}
