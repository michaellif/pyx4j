/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 9, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.util;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;

import com.propertyvista.config.deployment.VistaNamespaceResolver;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;

public class VistaTestsServerSideConfigurationForNamespace extends VistaTestsServerSideConfiguration {

    public VistaTestsServerSideConfigurationForNamespace(DatabaseType databaseType) {
        super(databaseType);
    }

    @Override
    public NamespaceResolver getNamespaceResolver(HttpServletRequest httpRequest) {
        return VistaNamespaceResolver.instance().getNamespaceResolver(httpRequest);
    }

}
