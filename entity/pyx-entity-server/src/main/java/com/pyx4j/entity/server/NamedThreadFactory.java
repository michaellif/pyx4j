/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on May 26, 2015
 * @author vlads
 */
package com.pyx4j.entity.server;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private final ThreadGroup threadGroup;

    private final ClassLoader contextClassLoader;

    private final String threadSufixName;

    private int threadSeqNumber = 0;

    public NamedThreadFactory(String threadGroupName, String threadSufixName) {
        this(new ThreadGroup(threadGroupName), null, threadSufixName);
    }

    public NamedThreadFactory(ThreadGroup threadGroup, ClassLoader contextClassLoader, String threadSufixName) {
        this.threadGroup = threadGroup;
        this.contextClassLoader = contextClassLoader;
        this.threadSufixName = (threadSufixName == null) ? "Thread" : threadSufixName;

    }

    private synchronized int nextThreadNum() {
        return ++threadSeqNumber;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(threadGroup, r, threadGroup.getName() + "-" + threadSufixName + "-" + nextThreadNum());
        if (contextClassLoader != null) {
            thread.setContextClassLoader(contextClassLoader);
        }
        return thread;
    }

}
