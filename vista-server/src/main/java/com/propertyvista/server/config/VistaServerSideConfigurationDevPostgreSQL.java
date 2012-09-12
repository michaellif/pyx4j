/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.IPersistenceConfiguration;

import com.propertyvista.misc.VistaTODO;

public class VistaServerSideConfigurationDevPostgreSQL extends VistaServerSideConfigurationDev {

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationPostgreSQL() {
            @Override
            public String dbName() {
                if (VistaTODO.codeBaseIsProdBranch) {
                    return super.dbName() + "_prod";
                } else {
                    return super.dbName();
                }
            }
        };
    }
}
