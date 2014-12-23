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
package com.propertyvista.server.config.filter;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.NamespaceData;
import com.pyx4j.config.server.NamespaceDataResolver;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.filter.namespace.VistaApplicationResolverHelper;
import com.propertyvista.server.config.filter.namespace.VistaNamespaceResolverHelper;

public class VistaNamespaceDataResolver extends NamespaceDataResolver {

    private VistaNamespaceData namespaceData = null;

    private VistaNamespaceDataResolver(HttpServletRequest httprequest) {
        super(httprequest);
    }

    public static VistaNamespaceDataResolver create(HttpServletRequest httpRequest) {
        return new VistaNamespaceDataResolver(httpRequest);
    }

    @Override
    public NamespaceData getNamespaceData() {
        getVistaApplication();
        getVistaNamespace();
        return namespaceData;
    }

    public VistaApplication getVistaApplication() {
        if (!isApplicationDefined()) {
            initialize();
            this.namespaceData.setApplication(VistaApplicationResolverHelper.getApplication(httpRequest));
        }
        return this.namespaceData.getApplication();
    }

    public String getVistaNamespace() {
        if (!isNamespaceDefined()) {
            initialize();
            this.namespaceData.setNamespace(VistaNamespaceResolverHelper.getNamespace(httpRequest));
        }
        return this.namespaceData.getNamespace();
    }

    private boolean isApplicationDefined() {
        return (this.namespaceData != null && this.namespaceData.getApplication() != null);
    }

    private boolean isNamespaceDefined() {
        return (this.namespaceData != null && this.namespaceData.getNamespace() != null);
    }

    private void initialize() {
        if (namespaceData == null) {
            namespaceData = new VistaNamespaceData();
        }
    }

}
