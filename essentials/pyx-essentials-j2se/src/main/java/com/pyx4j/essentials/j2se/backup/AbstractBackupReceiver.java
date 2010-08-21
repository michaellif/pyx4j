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
 * Created on 2010-08-12
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.rpc.admin.BackupRecordsResponse;
import com.pyx4j.essentials.rpc.admin.BackupRequest;

public abstract class AbstractBackupReceiver implements BackupReceiver {

    private static final Logger log = LoggerFactory.getLogger(AbstractBackupReceiver.class);

    public long totalRecords;

    protected AbstractBackupReceiver() {
        EntityFactory.setImplementation(new ServerEntityFactory());
        EntityImplGenerator.generateOnce(false);
    }

    protected abstract BackupRecordsResponse get(BackupRequest request);

    public void copy(BackupConsumer consumer, Class<? extends IEntity>... entityClass) {
        for (Class<? extends IEntity> clazz : entityClass) {
            EntityMeta entityMeta = EntityFactory.getEntityMeta(clazz);
            copy(consumer, entityMeta.getPersistenceName(), getMaxResponceSize(clazz));
        }
    }

    protected int getMaxResponceSize(Class<? extends IEntity> clazz) {
        return BackupRequest.DEFAULT_BATCH_SIZE;
    }

    @Override
    public void copy(BackupConsumer consumer, String persistenceName, int maxResponceSize) {
        int thisCount = 0;
        BackupRequest request = new BackupRequest();
        request.setEntityKind(persistenceName);
        request.setResponceSize(maxResponceSize);
        BackupRecordsResponse responce;
        do {
            responce = get(request);
            if (responce.size() > 0) {
                consumer.save(responce.getRecords());
            }
            thisCount += responce.size();
            request.setEncodedCursorRefference(responce.getEncodedCursorRefference());
        } while (responce.getEncodedCursorRefference() != null);

        log.info("Got {} of {}", thisCount, persistenceName);
        totalRecords += thisCount;
    }

}
