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
package ru.org.sevn.common.jmx;

public class App extends AbstractApp {

    public static String NAME = "ru.org.sevn.common.jmx:type=App";
    
    public App(Runnable onstop) {
        super(NAME, onstop);
    }
    
    public void runTask() {
        System.out.println("rrrrrrrrrrr");
    }
            /*
        -Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9999
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
        */
    public static void main(String[] args) {
        final JMXLocal svrjmx = new JMXLocal(9999);
        if (!svrjmx.runApp(new App(svrjmx.getStopRunnable()))) {
            svrjmx.stopQuiet();
        }
    }

    @Override
    public void cmd(String[] cmd) {
    }
    
}
