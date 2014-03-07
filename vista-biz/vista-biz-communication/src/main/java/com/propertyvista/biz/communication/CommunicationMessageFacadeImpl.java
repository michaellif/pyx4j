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

import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.EndpointType;

public class CommunicationMessageFacadeImpl implements CommunicationMessageFacade {
    private static class SystemEndpointCacheKey {
        static String getCacheKey(EndpointType epType) {
            return String.format("%s_%s", SystemEndpoint.class.getName(), epType);
        }
    }

    public CommunicationMessageFacadeImpl() {
        cacheSystemEndpoints();
    }

    private void cacheSystemEndpoints() {
        EntityQueryCriteria<SystemEndpoint> criteria = EntityQueryCriteria.create(SystemEndpoint.class);
        List<SystemEndpoint> predefinedEps = Persistence.service().query(criteria);
        if (predefinedEps != null) {
            for (SystemEndpoint ep : predefinedEps)
                CacheService.put(SystemEndpointCacheKey.getCacheKey(ep.type().getValue()), ep);
        }
    }

    @Override
    public SystemEndpoint getSystemEndpointFromCache(EndpointType epType) {
        SystemEndpoint ep = CacheService.get(SystemEndpointCacheKey.getCacheKey(epType));
        return ep;
    }
}
