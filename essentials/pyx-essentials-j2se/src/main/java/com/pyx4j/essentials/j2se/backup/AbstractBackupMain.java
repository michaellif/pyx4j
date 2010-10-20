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

import java.util.Date;
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
        try {
            final Date backupDate = new Date();

            AbstractBackupReceiver receiver;

            if (isFile(argFrom)) {
                receiver = createFileBackupReceiver(argFrom);
            } else {
                receiver = new ServerBackupReceiver(createConnection("Backup Source", argFrom)) {
                    @Override
                    protected int getMaxResponceSize(Class<? extends IEntity> clazz) {
                        return AbstractBackupMain.this.getMaxResponceSize(clazz);
                    }

                    @Override
                    protected String createReport() {
                        return AbstractBackupMain.this.createReport(backupDate, this);
                    }
                };
            }

            BackupConsumer consumer;
            if (isFile(argTo)) {
                consumer = createFileBackupConsumer(argTo, backupDate);
            } else {
                consumer = new ServerBackupConsumer(createConnection("Backup Consumer", argTo));
            }

            long start = System.currentTimeMillis();
            try {
                receiver.start();
                consumer.start();
                receiver.copy(consumer, allClasses());
                receiver.completed();
            } finally {
                receiver.end();
                consumer.end();
                log.info("Backup of {} records processing time {}", receiver.totalRecords, TimeUtils.minutesSince(start));
            }
        } catch (Throwable t) {
            log.error("Backup error", t);
            System.exit(1);
        }
    }

    protected abstract boolean isFile(String arg);

    protected abstract J2SEService createConnection(String name, String arg);

    protected boolean isXMLBackupFile(String name) {
        return name.endsWith(".xml") || name.endsWith(".xml.zip");
    }

    protected AbstractBackupReceiver createFileBackupReceiver(String name) {
        if (isXMLBackupFile(name)) {
            return new LocalXMLBackupReceiver(name);
        } else {
            return new LocalDatastoreBackupReceiver(name);
        }
    }

    protected BackupConsumer createFileBackupConsumer(String name, Date backupDate) {
        if (isXMLBackupFile(name)) {
            return new LocalXMLBackupConsumer(name, true, backupDate);
        } else {
            return new LocalDatastoreBackupConsumer(name, true, backupDate);
        }
    }

    public abstract Class<? extends IEntity>[] allClasses();

    protected int getMaxResponceSize(Class<? extends IEntity> clazz) {
        return BackupRequest.DEFAULT_BATCH_SIZE;
    }

    protected String createReport(Date backupDate, ServerBackupReceiver receiver) {
        return null;
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
