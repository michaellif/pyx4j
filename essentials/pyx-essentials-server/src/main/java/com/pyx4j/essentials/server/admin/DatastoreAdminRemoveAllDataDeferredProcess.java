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
 * Created on 2010-09-30
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

public class DatastoreAdminRemoveAllDataDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -6192633946704509716L;

    protected volatile boolean canceled;

    private boolean compleate;

    private int total;

    private LinkedList<Class<? extends IEntity>> queue;

    private String encodedCursorRefference;

    private Class<? extends IEntity> entityClass;

    private int entityCount;

    private final StringBuilder message = new StringBuilder();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute() {
        if (canceled || compleate) {
            return;
        }
        long start = System.currentTimeMillis();
        if (queue == null) {
            queue = new LinkedList();
            queue.addAll(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders().getEntityToDelete());
            total = queue.size();
        }
        while (!queue.isEmpty()) {
            if (System.currentTimeMillis() - start > Consts.SEC2MSEC * 15) {
                return;
            }
            if (entityClass == null) {
                entityClass = queue.peek();
                entityCount = 0;
            }
            EntityQueryCriteria criteria = EntityQueryCriteria.create(entityClass);
            ICursorIterator<String> ci = PersistenceServicesFactory.getPersistenceService().queryKeys(encodedCursorRefference, criteria);
            List<String> primaryKeys = new Vector<String>();
            while (ci.hasNext()) {
                primaryKeys.add(ci.next());
                boolean quotaExceeded = System.currentTimeMillis() - start > Consts.SEC2MSEC * 15;
                if ((primaryKeys.size() > 100) || (quotaExceeded)) {
                    PersistenceServicesFactory.getPersistenceService().delete(entityClass, primaryKeys);
                    entityCount += primaryKeys.size();
                    primaryKeys.clear();
                }
                if (quotaExceeded) {
                    encodedCursorRefference = ci.encodedCursorReference();
                    return;
                }
            }
            if (primaryKeys.size() > 0) {
                PersistenceServicesFactory.getPersistenceService().delete(entityClass, primaryKeys);
                entityCount += primaryKeys.size();
            }
            EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
            message.append("Removed " + entityCount + " " + entityMeta.getCaption() + "(s)\n");

            queue.poll();
            entityCount = 0;
            encodedCursorRefference = null;
            entityClass = null;
        }
        compleate = true;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
        if (queue != null) {
            r.setProgress(total - queue.size());
            r.setProgressMaximum(total);
        }
        if (compleate) {
            r.setMessage(message.toString());
            r.setCompleted();
        } else {
            if (canceled) {
                r.setCanceled();
            }
        }
        return r;
    }

}
