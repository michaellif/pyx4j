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

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

public class SessionsPurgeDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = 2801629609391105201L;

    private final static Logger log = LoggerFactory.getLogger(SessionsPurgeDeferredProcess.class);

    private String encodedCursorRefference;

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
        EntityQueryCriteria<GaeStoredSession> criteria = EntityQueryCriteria.create(GaeStoredSession.class);
        if (inactiveTime > 0) {
            criteria.add(new PropertyCriterion(criteria.meta()._expires(), Restriction.LESS_THAN, inactiveTime));
        }
        List<Long> primaryKeys = new Vector<Long>();
        ICursorIterator<Long> keysIter = PersistenceServicesFactory.getPersistenceService().queryKeys(encodedCursorRefference, criteria);
        while (keysIter.hasNext()) {
            Long key = keysIter.next();
            primaryKeys.add(key);
            count++;
            if (canceled) {
                log.debug("fetch canceled");
                break;
            }
            boolean quotaExceeded = System.currentTimeMillis() - start > Consts.SEC2MSEC * 15;
            if ((primaryKeys.size() > 20) || (quotaExceeded)) {
                PersistenceServicesFactory.getPersistenceService().delete(GaeStoredSession.class, primaryKeys);
                primaryKeys.clear();
                if (!quotaExceeded) {
                    quotaExceeded = System.currentTimeMillis() - start > Consts.SEC2MSEC * 15;
                }
            }
            if (quotaExceeded) {
                log.warn("Executions time quota exceeded {}", System.currentTimeMillis() - start);
                log.debug("fetch and delte will continue {}", count);
                encodedCursorRefference = keysIter.encodedCursorRefference();
                return;
            }
        }
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
        }
        return r;
    }

}
