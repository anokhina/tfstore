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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.org.sevn.common.jmx.AbstractApp;
import ru.org.sevn.common.jmx.JMXLocal;
import ru.org.sevn.common.solr.SolrIndexer;
import ru.org.sevn.common.util.WinExec;

public class App extends AbstractApp implements AppMBean {

    public static String NAME = "ru.org.sevn.tfstore:type=App";
    
    private final String solrUrl;
    private final String solrLocation;
    private final int solrPort;
    
    public App(File workDir, Runnable onstop, int solrPort, String solrLocation) {
        this(workDir, onstop, "http://localhost:"+solrPort+"/solr", solrLocation, solrPort, DEFAULT_INITIAL_DELAY, DEFAULT_PERIOD, DEFAULT_UNIT);
    }
    protected App(File workDir, Runnable onstop, String url) {
        this(workDir, onstop, url, null, -1, DEFAULT_INITIAL_DELAY, DEFAULT_PERIOD, DEFAULT_UNIT);
    }
    protected App(File workDir, Runnable onstop, String url, String sl, int solrPort, long initialDelay, long period, TimeUnit unit) {
        super(NAME, onstop, initialDelay, period, unit);
        this.storeDir = workDir;
        this.solrUrl = url;
        this.solrLocation = sl;
        this.solrPort = solrPort;
    }
    
    protected void startSolr() {
        System.out.println("startSolr");
        if (solrLocation != null) {
            try {
                WinExec.runCmd(new File(solrLocation).getAbsolutePath() + " start -p " + solrPort + " -m 256m ");
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    protected void stopSolr() {
        System.out.println("stopSolr");
        if (solrLocation != null) {
            try {
                WinExec.runCmd(new File(solrLocation).getAbsolutePath() + " stop -p " + solrPort);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }

    @Override
    protected void init() {
        startSolr();
    }
    
    @Override
    protected void clean() {
        stopSolr();
    }

    public static final String SOLR_COLLECTION = "fstore";
    public static final String DEFAULT_IN_DIR = "fsdata";
    private final File storeDir;
    
    public void run() {
        System.out.println("create distributer");
        //TODO
        /*
        SolrIndexer indexer = new SolrIndexer(solrUrl, SOLR_COLLECTION);
        StoreDistribute storeDistribute = new StoreDistribute(storeDir, indexer);
*/
//        storeDistribute.run();
    }
    
    public static void main(String[] args) {
        
        final JMXLocal svrjmx = new JMXLocal(9999);
        App m = new App(new File(DEFAULT_IN_DIR), svrjmx.getStopRunnable(), 7777, "D:/Java/solr-6.6.0/bin/solr.cmd");
        
        if (!svrjmx.runApp(m)) {
            svrjmx.stopQuiet();
        }
    }
}
