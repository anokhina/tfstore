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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractApp implements AppMBean, Runnable {
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;
    public static final long DEFAULT_PERIOD = 6;
    public static final long DEFAULT_INITIAL_DELAY = 0;
    
    private final Runnable onstop;
    private final String name;
    
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;
    private ScheduledFuture<?> scheduledFuture;
    
    public AbstractApp(String name, Runnable onstop) {
        this(name, onstop, DEFAULT_INITIAL_DELAY, DEFAULT_PERIOD, DEFAULT_UNIT);
    }
    public AbstractApp(String name, Runnable onstop, long initialDelay, long period, TimeUnit unit) {
        this.name = name;
        this.onstop = onstop;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }
    
    private void runIt(long initialDelay) {
        scheduledFuture = executor.scheduleAtFixedRate(this, initialDelay, period, unit);
    }
    
    public void forceUpdate() {
        if (executor != null) {
            executor.schedule(new Runnable() {
                public void run() { 
                    if (scheduledFuture == null || scheduledFuture.cancel(false)) {
                        runIt(0);
                    }
                }
            }, 0, TimeUnit.SECONDS);
        }
    }
    
    @Override
    public String getObjectName() {
        return name;
    }
    
    @Override
    public void stop() {
        System.out.println("remote stop called");
        scheduledFuture.cancel(false);
        scheduledFuture = null;
        executor = null;
        clean();
        if (onstop != null) {
            onstop.run();
        }
    }
    
    protected void clean() {}
    
    @Override
    public boolean isRunning() {
        return (scheduledFuture != null);
    }

    @Override
    public void start() {
        System.out.println("remote start called");
        init();
        runIt(initialDelay);
    }
    
    protected void init() {}
}
