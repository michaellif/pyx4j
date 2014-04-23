/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.List;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.CommunicationGroup.EndpointGroup;

public class CommunicationMessageFacadeImpl implements CommunicationMessageFacade {
    private static class CommunicationGroupCacheKey {
        static String getCacheKey(EndpointGroup epType) {
            return String.format("%s_%s", CommunicationGroup.class.getName(), epType);
        }
    }

    public CommunicationMessageFacadeImpl() {
        cacheCommunicationGroups();
    }

    private void cacheCommunicationGroups() {
        EntityQueryCriteria<CommunicationGroup> criteria = EntityQueryCriteria.create(CommunicationGroup.class);
        List<CommunicationGroup> predefinedEps = Persistence.service().query(criteria);
        if (predefinedEps != null) {
            for (CommunicationGroup ep : predefinedEps)
                CacheService.put(CommunicationGroupCacheKey.getCacheKey(ep.type().getValue()), ep);
        }
    }

    @Override
    public CommunicationGroup getCommunicationGroupFromCache(EndpointGroup epType) {
        CommunicationGroup ep = CacheService.get(CommunicationGroupCacheKey.getCacheKey(epType));
        return ep;
    }
}
