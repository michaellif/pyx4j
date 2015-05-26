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
package com.pyx4j.entity.server.textsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Executables;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

class UpdateQueueTask implements Runnable {

    private static Logger log = LoggerFactory.getLogger(UpdateQueueTask.class);

    private final UpdateQueueItem item;

    UpdateQueueTask(UpdateQueueItem item) {
        this.item = item;
    }

    @Override
    public void run() {
        try {
            Executable<Void, RuntimeException> update = new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    TextSearchIndexManager.instance().updateAllIndexes(item.getIdentityStub());
                    return null;
                }
            };

            if (item.getNamespace() != null) {
                update = Executables.wrapInTargetNamespace(item.getNamespace(), update);
            }
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(update);
        } catch (Throwable e) {
            log.error("Failed to update index for {}", this);
        }

    }

}
