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

import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.Stats;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.essentials.rpc.admin.AdminServices;
import com.pyx4j.essentials.rpc.admin.NetworkSimulation;
import com.pyx4j.essentials.server.deferred.DeferredProcessServicesImpl;
import com.pyx4j.essentials.server.dev.NetworkSimulationServiceFilter;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

public class AdminServicesImpl implements AdminServices {

    static long inactiveTime() {
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
        public String execute(VoidSerializable request) {
            EntityQueryCriteria<GaeStoredSession> criteria = EntityQueryCriteria.create(GaeStoredSession.class);
            int countAll = PersistenceServicesFactory.getPersistenceService().count(criteria);
            criteria.add(new PropertyCriterion(criteria.meta()._expires(), Restriction.LESS_THAN, inactiveTime()));
            int countExpired = PersistenceServicesFactory.getPersistenceService().count(criteria);
            return MessageFormat.format("Total sessions: {0}\nExpired sessions: {1}\n", countAll, countExpired);
        }
    }

    public static class PurgeExpiredSessionsImpl implements AdminServices.PurgeExpiredSessions {

        @Override
        public String execute(VoidSerializable request) {
            return DeferredProcessServicesImpl.register(new SessionsPurgeDeferredProcess(false));
        }

    }

    public static class PurgeAllSessionsImpl implements AdminServices.PurgeAllSessions {

        @Override
        public String execute(VoidSerializable request) {
            return DeferredProcessServicesImpl.register(new SessionsPurgeDeferredProcess(true));
        }

    }

    public static class MemcacheClearImpl implements AdminServices.MemcacheClear {

        @Override
        public VoidSerializable execute(VoidSerializable request) {
            MemcacheServiceFactory.getMemcacheService().clearAll();
            return null;
        }

    }

    public static class EntityMemCacheTogleImpl implements AdminServices.EntityMemCacheTogle {

        @Override
        public VoidSerializable execute(VoidSerializable request) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static class MemcacheStatisticsImpl implements AdminServices.MemcacheStatistics {

        @Override
        public String execute(VoidSerializable request) {
            // TODO  EntityCacheServiceGAE.isDisabled()
            Stats stats = MemcacheServiceFactory.getMemcacheService().getStatistics();
            return MessageFormat.format("Alive items: {0}\nMemcache size: {1} Bytes\nHit Count: {2}\nMiss Count: {3}\nEntity MemCache: {4}",
                    stats.getItemCount(), stats.getTotalItemBytes(), stats.getHitCount(), stats.getMissCount(), "ON");
        }
    }

    public static class NetworkSimulationSetImpl implements AdminServices.NetworkSimulationSet {

        @Override
        public IEntity execute(IEntity request) {
            NetworkSimulationServiceFilter.setNetworkSimulationConfig((NetworkSimulation) request);
            return NetworkSimulationServiceFilter.getNetworkSimulationConfig();
        }

    }

    public static class NetworkSimulationRetrieveImpl implements AdminServices.NetworkSimulationRetrieve {

        @Override
        public NetworkSimulation execute(VoidSerializable request) {
            return NetworkSimulationServiceFilter.getNetworkSimulationConfig();
        }

    }

}
