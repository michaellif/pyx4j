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
import com.propertyvista.config.BmoInterfaceConfiguration;
import com.propertyvista.config.VistaInterfaceCredentials;

class BmoInterfaceConfigurationSimulatorCustom extends BmoInterfaceConfiguration {

    private final AbstractVistaServerSideConfiguration config;

    BmoInterfaceConfigurationSimulatorCustom(AbstractVistaServerSideConfiguration config) {
        this.config = config;
    }

    @Override
    public String bmoMailboxNumber() {
        return config.getConfigProperties().getValue("simulator.bmoPool.mailboxNumber", "TST12345");
    }

    @Override
    public String sftpHost() {
        return config.getConfigProperties().getValue("simulator.bmoPool.sftpHost", "qa.birchwoodsoftwaregroup.com");
    }

    @Override
    public int sftpPort() {
        return config.getConfigProperties().getIntegerValue("simulator.bmoPool.sftpPort", 8823);
    }

    @Override
    public Credentials sftpCredentials() {
        String fileName = config.getConfigProperties().getValue("simulator.bmoPool.sftpCredentialsFileName", VistaInterfaceCredentials.bmoMailBoxPoolSimulator);
        return CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), fileName));
    }

}
