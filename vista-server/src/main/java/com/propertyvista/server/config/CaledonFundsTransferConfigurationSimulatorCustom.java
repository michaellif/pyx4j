/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.File;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.CaledonFundsTransferConfiguration;
import com.propertyvista.config.VistaInterfaceCredentials;

class CaledonFundsTransferConfigurationSimulatorCustom extends CaledonFundsTransferConfiguration {

    private final AbstractVistaServerSideConfiguration config;

    CaledonFundsTransferConfigurationSimulatorCustom(AbstractVistaServerSideConfiguration config) {
        this.config = config;
    }

    @Override
    public String getIntefaceCompanyId() {
        String companyId = "Test";
        if (config.enviromentId() != null) {
            companyId += config.enviromentId();
        }
        return config.getConfigProperties().getValue("simulator.fundsTransfer.intefaceCompanyId", companyId);
    }

    @Override
    public String sftpHost() {
        return config.getConfigProperties().getValue("simulator.fundsTransfer.sftpHost", "qa.birchwoodsoftwaregroup.com");
    }

    @Override
    public int sftpPort() {
        return config.getConfigProperties().getIntegerValue("simulator.fundsTransfer.sftpPort", 8823);
    }

    @Override
    public Credentials sftpCredentials() {
        String fileName = config.getConfigProperties().getValue("simulator.fundsTransfer.sftpCredentialsFileName",
                VistaInterfaceCredentials.caledonFundsTransferSimulator);
        return CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), fileName));
    }

}
