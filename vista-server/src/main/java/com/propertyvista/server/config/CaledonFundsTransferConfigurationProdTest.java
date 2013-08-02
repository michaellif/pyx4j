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

class CaledonFundsTransferConfigurationProdTest extends CaledonFundsTransferConfiguration {

    private final AbstractVistaServerSideConfiguration config;

    CaledonFundsTransferConfigurationProdTest(AbstractVistaServerSideConfiguration config) {
        this.config = config;
    }

    @Override
    public String getIntefaceCompanyId() {
        return config.getConfigProperties().getValue("fundsTransfer.intefaceCompanyId", "BIRCHWOODTEST");
    }

    @Override
    public String sftpHost() {
        return config.getConfigProperties().getValue("fundsTransfer.sftpHost", "apato.caledoncard.com");
    }

    @Override
    public int sftpPort() {
        return config.getConfigProperties().getIntegerValue("fundsTransfer.sftpPort", 22);
    }

    @Override
    public Credentials sftpCredentials() {
        return CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), VistaInterfaceCredentials.caledonFundsTransfer));
    }

}
