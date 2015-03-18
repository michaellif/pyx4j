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
import com.propertyvista.domain.security.common.VistaApplication;
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

        //namespaceData.setSpecialURL(SpecialURLMaping.resolve(httpRequest));

        // Resolve application
        VistaApplication app = VistaApplicationResolverHelper.getApplication(httpRequest);
        namespaceData.setApplication(app);

        // Based on resolved application, set namespace in some cases
        if (app != null) {
            switch (app) {
            case operations:
            case interfaces:
                namespaceData.setNamespace(VistaNamespace.operationsNamespace);
                return;
            case noApp:
            case onboarding:
                namespaceData.setNamespace(VistaNamespace.noNamespace);
                return;
            default:
                break;
            }
        }

        // Set namespace in default cases
        namespaceData.setNamespace(VistaNamespaceResolverHelper.getNamespace(httpRequest));

        // Set customerPmcDnsName
        namespaceData.setCustomerDnsName(VistaPmcDnsNameResolverHelper.getCustomerPmcDnsNameForApplication(httpRequest, app));
    }
}
