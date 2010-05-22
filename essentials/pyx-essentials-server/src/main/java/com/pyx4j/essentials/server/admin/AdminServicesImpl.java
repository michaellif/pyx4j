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

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.essentials.rpc.admin.AdminServices;
import com.pyx4j.essentials.server.deferred.DeferredProcessServicesImpl;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

public class AdminServicesImpl implements AdminServices {

    private static long inactiveTime() {
        int sessionTimeoutSeconds = 0;
        if (Context.getSession() != null) {
            sessionTimeoutSeconds = Context.getSession().getMaxInactiveInterval();
        }
        if (sessionTimeoutSeconds <= 0) {
            sessionTimeoutSeconds = 24 * Consts.HOURS2SEC;
        }
        return System.currentTimeMillis() - sessionTimeoutSeconds * Consts.SEC2MILLISECONDS;
    }

    public static class CountSessionsImpl implements AdminServices.CountSessions {

        @Override
        public String execute(Boolean allSessions) {
            EntityQueryCriteria<GaeStoredSession> criteria = EntityQueryCriteria.create(GaeStoredSession.class);
            if ((allSessions == null) || (!allSessions)) {
                criteria.add(new PropertyCriterion(criteria.meta()._expires(), Restriction.LESS_THAN, inactiveTime()));
            }
            int count = PersistenceServicesFactory.getPersistenceService().count(criteria);
            return String.valueOf(count);
        }
    }

    public static class PurgeExpiredSessionsImpl implements AdminServices.PurgeExpiredSessions {

        @Override
        public String execute(VoidSerializable request) {
            return DeferredProcessServicesImpl.register(new SessionsPurgeDeferredProcess(true));
        }

    }

    public static class PurgeAllSessionsImpl implements AdminServices.PurgeAllSessions {

        @Override
        public String execute(VoidSerializable request) {
            return DeferredProcessServicesImpl.register(new SessionsPurgeDeferredProcess(true));
        }

    }

}
