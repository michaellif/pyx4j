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
 * Created on Aug 4, 2015
 * @author vlads
 */
package com.pyx4j.gwt.server.deferred;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.NamedThreadFactory;

// TODO remove public when adding Modules. 
public class DeferredProcessExecutors {

    private static Logger log = LoggerFactory.getLogger(DeferredProcessExecutors.class);

    private static class ExecutorHolder {

        ThreadGroup threadGroup;

        ExecutorService executorService;

        void shutdown() {
            try {
                executorService.shutdownNow();
                threadGroup.interrupt();
            } catch (Throwable ignore) {
            }
        }

    }

    private final Map<String, ExecutorHolder> executors = new HashMap<>();

    private DeferredProcessExecutors() {
    }

    private static class SingletonHolder {
        public static final DeferredProcessExecutors INSTANCE = new DeferredProcessExecutors();
    }

    static DeferredProcessExecutors instance() {
        return SingletonHolder.INSTANCE;
    }

    public static void shutdown() {
        try {
            for (ExecutorHolder executorService : instance().executors.values()) {
                executorService.shutdown();
            }
            instance().executors.clear();
        } catch (Throwable e) {
        }
    }

    synchronized ExecutorService getExecutorService(String threadPoolName) {
        ExecutorHolder executorHolder = executors.get(threadPoolName);
        if (executorHolder == null) {
            int threads = ServerSideConfiguration.instance().getConfigProperties().getIntegerValue("deferredProcess." + threadPoolName, 10);
            executorHolder = new ExecutorHolder();
            executorHolder.threadGroup = new ThreadGroup("DeferredProcess." + threadPoolName);
            executorHolder.executorService = Executors.newFixedThreadPool(threads + 1, new NamedThreadFactory(executorHolder.threadGroup, null, "Thread"));
            executors.put(threadPoolName, executorHolder);
            log.info("Created new ThreadPool {} of size {}", executorHolder.threadGroup.getName(), threads);
        }
        return executorHolder.executorService;
    }
}
