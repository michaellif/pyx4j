/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Aug 21, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.backup;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.rpc.admin.BackupRequest;
import com.pyx4j.rpc.j2se.J2SEService;

public abstract class AbstractBackupMain {

    private static final Logger log = LoggerFactory.getLogger(AbstractBackupMain.class);

    public void execute(String argFrom, String argTo) {
        AbstractBackupReceiver receiver;

        if (isFile(argFrom)) {
            receiver = new LocalDatastoreBackupReceiver(argFrom);
        } else {
            receiver = new ServerBackupReceiver(createConnection("Backup Source", argFrom)) {
                @Override
                protected int getMaxResponceSize(Class<? extends IEntity> clazz) {
                    return AbstractBackupMain.this.getMaxResponceSize(clazz);
                }
            };
        }

        BackupConsumer consumer;
        if (isFile(argTo)) {
            consumer = createBackupConsumer(argTo);
        } else {
            consumer = new ServerBackupConsumer(createConnection("Backup Consumer", argTo));
        }

        long start = System.currentTimeMillis();
        try {
            receiver.start();
            consumer.start();
            receiver.copy(consumer, allClasses());
        } finally {
            receiver.end();
            consumer.end();
            log.info("Backup of {} records processing time {}", receiver.totalRecords, TimeUtils.minutesSince(start));
        }
    }

    protected abstract boolean isFile(String arg);

    protected abstract J2SEService createConnection(String name, String arg);

    protected BackupConsumer createBackupConsumer(String name) {
        if (name.endsWith(".xml")) {
            return new LocalXMLBackupConsumer(name, true);
        } else {
            return new LocalDatastoreBackupConsumer(name, true);
        }
    }

    public abstract Class<? extends IEntity>[] allClasses();

    protected int getMaxResponceSize(Class<? extends IEntity> clazz) {
        return BackupRequest.DEFAULT_BATCH_SIZE;
    }

    public static void addAll(List<Class<? extends IEntity>> list, Class<? extends IEntity>... entityClasses) {
        for (Class<? extends IEntity> entityClass : entityClasses) {
            if (list.contains(entityClass)) {
                throw new Error("Duplicate class " + entityClass.getName());
            }
            list.add(entityClass);
        }
    }
}
