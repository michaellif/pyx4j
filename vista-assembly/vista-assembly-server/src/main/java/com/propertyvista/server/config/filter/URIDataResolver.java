/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.NamespaceResolver;

public abstract class URIDataResolver<T> implements NamespaceResolver, ApplicationResolver<T> {

    protected final HttpServletRequest httpRequest;

    public URIDataResolver(ServletRequest request) {
        this((HttpServletRequest) request);
    }

    public URIDataResolver(HttpServletRequest httprequest) {
        this.httpRequest = httprequest;
    }

    @Override
    public abstract String getNamespace(HttpServletRequest httpRequest);

    @Override
    public abstract T getApplication();

}
