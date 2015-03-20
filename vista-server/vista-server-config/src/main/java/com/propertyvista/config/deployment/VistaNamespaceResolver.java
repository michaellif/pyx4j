/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 19, 2015
 * @author vlads
 */
package com.propertyvista.config.deployment;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.NamespaceData;
import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;

public class VistaNamespaceResolver {

    private static String cacheNamespace = "_urls_";

    private VistaApplicationContextResolver applicationContextResolver;

    private VistaNamespaceResolver() {
        applicationContextResolver = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).createApplicationContextResolver();
    }

    private static class SingletonHolder {
        public static final VistaNamespaceResolver INSTANCE = new VistaNamespaceResolver();
    }

    public static VistaNamespaceResolver instance() {
        return SingletonHolder.INSTANCE;
    }

    //TODO Change API in pyx
    public NamespaceResolver getNamespaceResolver(final HttpServletRequest httpRequest) {
        return new NamespaceResolver() {
            @Override
            public NamespaceData getNamespaceData() {
                return instance().resolve(httpRequest);
            }

        };
    }

    public static void resetCache() {
        NamespaceManager.runInTargetNamespace(cacheNamespace, new Callable<Void>() {
            @Override
            public Void call() {
                return null;
            }
        });
    }

    public VistaApplicationContext resolve(final HttpServletRequest httpRequest) {
        return NamespaceManager.runInTargetNamespace(cacheNamespace, new Callable<VistaApplicationContext>() {
            @Override
            public VistaApplicationContext call() {
                VistaApplicationContext context = CacheService.get(HttpRequestUtils.getNamespaceDataCacheKey(httpRequest));
                // Cache resolution
                if (context == null) {
                    context = applicationContextResolver.resolve(httpRequest);
                    if (context == null) {
                        // have something in Cache
                        context = new VistaApplicationContext(null, null, null);
                    }
                    CacheService.put(HttpRequestUtils.getNamespaceDataCacheKey(httpRequest), context);
                }
                return context;
            }
        });
    }

}
