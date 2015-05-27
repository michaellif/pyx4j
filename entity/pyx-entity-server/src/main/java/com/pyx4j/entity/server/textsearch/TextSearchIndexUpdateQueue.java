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
 * Created on May 22, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.textsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.NamedThreadFactory;
import com.pyx4j.log4j.LoggerConfig;

class TextSearchIndexUpdateQueue {

    private final ScheduledThreadPoolExecutor scheduledThreadPool;

    // This holds unprocessed items to avoid duplicate processing
    private final Collection<UpdateQueueItem> queuedItems = Collections.synchronizedSet(new HashSet<UpdateQueueItem>());

    private class UpdateQueueTaskWrapper implements Runnable {

        private final UpdateQueueItem item;

        UpdateQueueTaskWrapper(UpdateQueueItem item) {
            this.item = item;
        }

        @Override
        public void run() {
            new UpdateQueueTask(item).run();
            queuedItems.remove(item);
        }

    }

    TextSearchIndexUpdateQueue() {
        scheduledThreadPool = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory(LoggerConfig.getContextName() + "-TextSearchIndexUpdate", "Updater"));
    }

    void queue(String namespace, IEntity identityStub, boolean fireChains) {
        UpdateQueueItem item = new UpdateQueueItem(namespace, identityStub, fireChains);
        if (!queuedItems.contains(item)) {
            queuedItems.add(item);
            scheduledThreadPool.schedule(new UpdateQueueTaskWrapper(item), fireChains ? 2 : 5, TimeUnit.SECONDS);
        }
    }

    /**
     * Process Queue without delay
     */
    void flushQueue() {
        scheduledThreadPool.getQueue().clear();
        List<UpdateQueueItem> list = new ArrayList<>(queuedItems);
        queuedItems.clear();
        for (UpdateQueueItem item : list) {
            new UpdateQueueTask(item).run();
        }
    }

    void shutdown() {
        flushQueue();
        scheduledThreadPool.shutdownNow();
    }

}
