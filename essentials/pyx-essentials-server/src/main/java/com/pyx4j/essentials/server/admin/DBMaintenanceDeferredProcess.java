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
package com.pyx4j.essentials.server.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.search.IndexedEntitySearch;
import com.pyx4j.entity.server.search.SearchResultIterator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.rpc.admin.DBMaintenanceRequest;
import com.pyx4j.essentials.rpc.admin.IDBMaintenanceProcessor;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.security.shared.SecurityController;

public class DBMaintenanceDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -7944873735643401186L;

    private final static Logger log = LoggerFactory.getLogger(DBMaintenanceDeferredProcess.class);

    private final DBMaintenanceRequest request;

    private IDBMaintenanceProcessor processor;

    private String encodedCursorRefference;

    protected final Class<? extends IEntity> entityClass;

    protected volatile boolean canceled;

    private int fetchCount = 0;

    private boolean fetchCompleate;

    public DBMaintenanceDeferredProcess(DBMaintenanceRequest request) {
        SecurityController.assertPermission(new EntityPermission(request.getCriteria().getEntityClass(), EntityPermission.READ));
        this.request = request;
        this.request.getCriteria().setPageSize(0);
        this.entityClass = request.getCriteria().getEntityClass();

        try {
            processor = request.getProcessor().newInstance();
        } catch (InstantiationException e) {
            log.error("create processor error {} {}", e, request.getProcessor().getName());
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error("create processor error {} {}", e, request.getProcessor().getName());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public void execute() {
        if (canceled || fetchCompleate) {
            return;
        }
        long start = System.currentTimeMillis();
        IndexedEntitySearch search = new IndexedEntitySearch(request.getCriteria());
        search.buildQueryCriteria();
        SearchResultIterator<IEntity> it = search.getResult(encodedCursorRefference);
        int currentFetchCount = 0;
        while (it.hasNext()) {
            IEntity ent = it.next();
            it.completeRetrieval();
            SecurityController.assertPermission(EntityPermission.permissionRead(ent.getValueClass()));
            boolean updateRequired = processor.process(ent);
            if (updateRequired) {
                SecurityController.assertPermission(EntityPermission.permissionUpdate(ent.getValueClass()));
                PersistenceServicesFactory.getPersistenceService().persist(ent);
            }
            fetchCount++;
            currentFetchCount++;
            if ((System.currentTimeMillis() - start > Consts.SEC2MSEC * 15) || (currentFetchCount > request.getBatchSize())) {
                log.warn("Executions time quota exceeded {}; rows {}", currentFetchCount, System.currentTimeMillis() - start);
                log.debug("fetch will continue rows {}", fetchCount);
                encodedCursorRefference = it.encodedCursorReference();
                return;
            }
            if (canceled) {
                log.debug("fetch canceled");
                break;
            }
        }
        log.debug("fetch completed rows {}", fetchCount);
        fetchCompleate = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
        r.setProgress(fetchCount);
        if (fetchCompleate) {
            r.setCompleted();
        } else {
            if (canceled) {
                r.setCanceled();
            }
        }
        return r;

    }

}
