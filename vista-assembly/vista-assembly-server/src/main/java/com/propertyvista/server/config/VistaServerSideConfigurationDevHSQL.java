/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2014
 * @author vlads
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.IPersistenceConfiguration;

/**
 * Used for in memory server stress tests (2x faster then PostgreSQL)
 */
public class VistaServerSideConfigurationDevHSQL extends VistaServerSideConfigurationDev {

    public static final boolean connectToQAProdCopy = false;

    public static final boolean connectToQA = false;

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaDBConfigurationHSQLMemory();
    }

}
