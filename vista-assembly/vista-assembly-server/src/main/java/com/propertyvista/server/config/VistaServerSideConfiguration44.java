/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2011
 * @author vlads
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.IPersistenceConfiguration;

public class VistaServerSideConfiguration44 extends VistaServerSideConfiguration {

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        return new VistaConfigurationPostgreSQL() {
            @Override
            public String dbName() {
                return "vista44";
            }

            @Override
            public String userName() {
                return "vista44";
            }

            @Override
            public String password() {
                return "vista44";
            }
        };
    }

    @Override
    public Integer enviromentId() {
        return 44;
    }

    @Override
    public boolean openIdRequired() {
        return getConfigProperties().getBooleanValue("openIdrequired", true);
    }

    @Override
    public int interfaceSSHDPort() {
        return 8824;
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Vista Support44\" <support.www44@birchwoodsoftwaregroup.com>";
    }

}
