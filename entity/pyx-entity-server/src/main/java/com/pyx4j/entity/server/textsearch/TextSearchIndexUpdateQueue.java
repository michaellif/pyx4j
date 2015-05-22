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

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Executables;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

class TextSearchIndexUpdateQueue implements Runnable {

    private Queue<UpdateQueueItem> updateQueue = new LinkedBlockingQueue<>();

    void queue(IEntity identityStub, String namespace) {
        UpdateQueueItem queueItem = new UpdateQueueItem(identityStub, namespace);

        processItem(queueItem);

        if (false) {
            if (!updateQueue.contains(queueItem)) {
                updateQueue.add(queueItem);
            }
        }
    }

    void flushQueue() {

    }

    void shutdown() {

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    private void processItem(final UpdateQueueItem queueItem) {
        Executable<Void, RuntimeException> update = new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                TextSearchIndexManager.instance().updateAllIndexes(queueItem.getIdentityStub());
                return null;
            }
        };

        if (queueItem.getNamespace() != null) {
            update = Executables.wrapInTargetNamespace(queueItem.getNamespace(), update);
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(update);
    }
}
