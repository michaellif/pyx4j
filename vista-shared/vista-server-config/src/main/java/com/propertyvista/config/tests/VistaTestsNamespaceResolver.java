/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.NamespaceResolver;

public class VistaTestsNamespaceResolver implements NamespaceResolver {

    public static final String demoNamespace = "vista";

    @Override
    public String getNamespace(HttpServletRequest httprequest) {
        return demoNamespace;
    }

}
