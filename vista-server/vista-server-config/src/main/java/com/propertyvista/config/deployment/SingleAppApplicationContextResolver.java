/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2015
 * @author vlads
 */
package com.propertyvista.config.deployment;

import javax.servlet.http.HttpServletRequest;

import com.propertyvista.domain.security.common.VistaApplication;

public class SingleAppApplicationContextResolver extends StandardApplicationContextResolver {

    private final String dnsName;

    private final VistaApplication application;

    public SingleAppApplicationContextResolver(String dnsName, VistaApplication application) {
        this.dnsName = dnsName;
        this.application = application;
    }

    @Override
    public VistaApplicationContext resolve(HttpServletRequest httpRequest) {
        // TODO Auto-generated method stub
        return null;
    }

}
