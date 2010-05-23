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
 * Created on 2010-05-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

public class SessionsPurgeDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = 2801629609391105201L;

    private final static Logger log = LoggerFactory.getLogger(SessionsPurgeDeferredProcess.class);

    private long inactiveTime;

    protected volatile boolean canceled;

    private int count = 0;

    private boolean compleate;

    public SessionsPurgeDeferredProcess(boolean all) {
        if (all) {
            inactiveTime = -1;
        } else {
            inactiveTime = AdminServicesImpl.inactiveTime();
        }
    }

    @Override
    public void execute() {
        if (canceled || compleate) {
            return;
        }
        long start = System.currentTimeMillis();
        EntityMeta entityMeta = EntityFactory.getEntityMeta(GaeStoredSession.class);
        Query query = new Query(entityMeta.getPersistenceName());
        query.setKeysOnly();
        if (inactiveTime > 0) {
            query.addFilter("_expires", Query.FilterOperator.LESS_THAN, inactiveTime);
        }
        List<Key> primaryKeys = new Vector<Key>();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        Iterator<Entity> iterable = results.asIterator();
        while (iterable.hasNext()) {
            Entity entity = iterable.next();
            primaryKeys.add(entity.getKey());
            count++;
            if (canceled) {
                log.debug("fetch canceled");
                break;
            }
            boolean quotaExceeded = System.currentTimeMillis() - start > Consts.SEC2MSEC * 15;
            if ((primaryKeys.size() > 20) || (quotaExceeded)) {
                datastore.delete(primaryKeys);
                primaryKeys.clear();
                if (!quotaExceeded) {
                    quotaExceeded = System.currentTimeMillis() - start > Consts.SEC2MSEC * 15;
                }
            }
            if (quotaExceeded) {
                log.warn("Executions time quota exceeded {}", System.currentTimeMillis() - start);
                log.debug("fetch and delte will continue {}", count);
                return;
            }
        }
        if (primaryKeys.size() > 0) {
            datastore.delete(primaryKeys);
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
        r.setProgress(count);
        if (canceled) {
            r.setCanceled();
        } else if (compleate) {
            r.setCompleted();
            r.setMessage(MessageFormat.format("Removed {0} sessions", count));
        }
        return r;
    }

}
