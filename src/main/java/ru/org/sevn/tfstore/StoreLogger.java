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

import java.io.BufferedWriter;
import java.util.Collection;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StoreLogger {
    private final File logDir;
    private File outFile;
    private Writer out;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public StoreLogger(File f) {
        this.logDir = f;
        logDir.mkdirs();
    }

    private Writer getWriter() throws FileNotFoundException, IOException {
        if (outFile == null) {
            outFile = new File(logDir, sdf.format(new Date()));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true), StandardCharsets.UTF_8));
        } else {
            String fname = sdf.format(new Date());
            if (!fname.equals(outFile.getName())) {
                Writer o = out;
                out = null;
                o.flush();
                o.close();
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true), StandardCharsets.UTF_8));
            }
        }
        return out;
    }
    
    public synchronized void log(String wpath, String part, String fullTitle, String uuid, Collection<String> tags) {
        JSONObject obj = new JSONObject();
        obj.put("w", wpath);
        obj.put("p", part);
        obj.put("t", fullTitle);
        obj.put("u", uuid);
        obj.put("ts", tags);
        
        try {
            Writer out = getWriter();
            out.write(obj.toString(2));
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(StoreLogger.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Can't log operation");
        }
    }
    
    public synchronized void close() {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(StoreLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
            out = null;
            outFile = null;
        }
    }
}
