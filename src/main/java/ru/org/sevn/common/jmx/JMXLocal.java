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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

public class JMXLocal {
    private final int port;
    private final String serverUrl;
    
    private JMXConnectorServer server;
    private MBeanServer mbs;
    
    public JMXLocal(int port) {
        this.port = port;
        this.serverUrl = "service:jmx:rmi://localhost/jndi/rmi://localhost:"+port+"/jmxrmi";
    }

    public int getPort() {
        return port;
    }

    public String getServerUrl() {
        return serverUrl;
    }
    
    public void start() throws RemoteException, MalformedURLException, IOException {
        if (server == null) {
            server = JMXConnectorServerFactory.newJMXConnectorServer(createUrl(), null, mbs);
            server.start();
            System.out.println("JMX started");
        }
    }
    
    private JMXServiceURL createUrl() throws RemoteException, MalformedURLException {
        LocateRegistry.createRegistry(port);
        mbs = ManagementFactory.getPlatformMBeanServer();
        return new JMXServiceURL(serverUrl);        
    }
    
    public boolean startQuiet() {
        try {
            start();
            return true;
        } catch (MalformedURLException ex) {
            Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void register(AppMBean amb) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        mbs.registerMBean(amb, new ObjectName(amb.getObjectName()));
    }
    
    public static boolean stopAppQuiet(int port, String name) {
        JMXLocal jmxl = new JMXLocal(port);
        return jmxl.stopAppQuiet(name);
    }
    
    public boolean stopAppQuiet(String name) {
        try {
            return stopApp(name);
        } catch (IOException ex) {
            Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public boolean stopApp(String name) throws MalformedURLException, IOException, MalformedObjectNameException {
        System.out.println("Connect to JMX service.");
        JMXServiceURL url = new JMXServiceURL(this.serverUrl);
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        try {
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName mbeanName = new ObjectName(name);
            AppMBean mbeanProxy = javax.management.JMX.newMBeanProxy(mbsc, mbeanName, AppMBean.class, true);

            System.out.println("Connected to: "+mbeanProxy.getObjectName()+", the app is "+(mbeanProxy.isRunning() ? "" : "not ")+"running");
            mbeanProxy.stop(); 
            return true;
        } finally {
            //jmxc.close();
        }
    }
    
    public boolean runApp(AppMBean amb) {
        if (startQuiet()) {
            try {
                register(amb);
                amb.start();
                return true;
            } catch (MalformedObjectNameException ex) {
                Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstanceAlreadyExistsException ex) {
                Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MBeanRegistrationException ex) {
                Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotCompliantMBeanException ex) {
                Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    public void stop() throws IOException {
        System.out.println("JMX stopped");
        if (server != null) {
            server.stop();
            server = null;
        }
    }
    
    public void stopQuiet() {
        try {
            stop();
        } catch (IOException ex) {
            Logger.getLogger(JMXLocal.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }
    
    public Runnable getStopRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                stopQuiet();
            }
        };
    }
}
