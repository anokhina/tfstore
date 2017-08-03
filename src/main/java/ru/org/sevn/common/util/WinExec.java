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
package ru.org.sevn.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class WinExec {
    private static class SPipe implements Runnable {
        private final OutputStream ostream;
        private final InputStream istream;
        
        public SPipe(InputStream is, OutputStream os) {
            istream = is;
            ostream = os;
        }

        public void run() {
            try {
                final byte[] buf = new byte[1024];
                for (int length = 0; (length = istream.read(buf)) != -1;) {
                    ostream.write(buf, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    } 

    public static String getBashCmd() {
        return "cmd";
    }
    
    public static int runCmds(String... cmds) throws InterruptedException, IOException {
        Process p = Runtime.getRuntime().exec(getBashCmd());
        new Thread(new SPipe(p.getErrorStream(), System.err)).start();
        new Thread(new SPipe(p.getInputStream(), System.out)).start();
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        for (String cm : cmds) {
            stdin.println(cm);
        }
        stdin.close();
        return p.waitFor();    
    }
    public static int runCmd(String cmd) throws InterruptedException, IOException {
        Process p = Runtime.getRuntime().exec(cmd);
        new Thread(new SPipe(p.getErrorStream(), System.err)).start();
        new Thread(new SPipe(p.getInputStream(), System.out)).start();
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        stdin.close();
        return p.waitFor();    
    }
}
