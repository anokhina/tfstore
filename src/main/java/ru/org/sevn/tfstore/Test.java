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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.noggit.JSONUtil;
import org.noggit.ObjectBuilder;

/**
 *
 * @author nika
 */
public class Test {
    public static class ZZZ {
        private String name = "test";
        private Date date = new Date();
        private boolean isok;
        
        public Map getProperties() {
            Map ret = new HashMap();
            ret.put("name", name);
            ret.put("date", date.getTime());
            ret.put("isok", isok);
            return ret;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public boolean isIsok() {
            return isok;
        }

        public void setIsok(boolean isok) {
            this.isok = isok;
        }
        
    }
public static void main(String[] args) throws IOException {
    ZZZ zzz = new ZZZ();
    String s = JSONUtil.toJSON(zzz.getProperties(), 2);
    System.out.println(s);
    System.out.println("--->" + ObjectBuilder.fromJSON(s).getClass().getName());
}    
}
