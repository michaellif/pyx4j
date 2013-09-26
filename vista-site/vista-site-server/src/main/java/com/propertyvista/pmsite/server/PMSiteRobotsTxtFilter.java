/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;

public class PMSiteRobotsTxtFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (VistaNamespace.noNamespace.equals(NamespaceManager.getNamespace())) {
            request.getServletContext().getRequestDispatcher("/robots-disallow.txt").forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
