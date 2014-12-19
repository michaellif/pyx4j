/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-06
 * @author vlads
 */
package com.propertyvista.server.config;

public class VistaConfigurationPostgreSQLDev2QA extends VistaConfigurationPostgreSQL {

    @Override
    public String dbHost() {
        return "qa.birchwoodsoftwaregroup.com";
    }

    @Override
    public int dbPort() {
        return 5432;
    }

    @Override
    public String dbName() {
        return "vista";
    }

    @Override
    public Ddl ddl() {
        return Ddl.disabled;
    }

}
