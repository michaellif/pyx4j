/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.namespace;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.NamespaceDataResolver;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.server.config.filter.utils.HttpRequestUtils;

public class VistaNamespaceDataResolver extends NamespaceDataResolver {

    private VistaNamespaceData namespaceData = null;

    private VistaNamespaceDataResolver(HttpServletRequest httprequest) {
        super(httprequest);
    }

    public static VistaNamespaceDataResolver create(HttpServletRequest httpRequest) {
        return new VistaNamespaceDataResolver(httpRequest);
    }

    public HttpServletRequest getHttpRequest() {
        return this.httpRequest;
    }

    @Override
    public VistaNamespaceData getNamespaceData() {

        if (namespaceData == null) {
            final String requestNamespace = NamespaceManager.getNamespace();
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            try {
                namespaceData = CacheService.get(HttpRequestUtils.getNamespaceDataCacheKey(httpRequest));

                if (namespaceData != null) {
                    return namespaceData;
                } else {
                    initNamespaceData();
                    CacheService.put(HttpRequestUtils.getNamespaceDataCacheKey(httpRequest), namespaceData);
                }

            } finally {
                NamespaceManager.setNamespace(requestNamespace);
            }
        }

        return namespaceData;
    }

    private void initNamespaceData() {
        namespaceData = new VistaNamespaceData();

        // Resolve application
        namespaceData.setApplication(VistaApplicationResolverHelper.getApplication(httpRequest));
        // TODO Set namespace based or solved app??
        namespaceData.setNamespace(VistaNamespaceResolverHelper.getNamespace(httpRequest));
    }

}
