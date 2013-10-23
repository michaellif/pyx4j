/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 14, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.File;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.BankingSimulatorConfiguration;

class BankingSimulatorConfigurationCustom extends BankingSimulatorConfiguration {

    private final AbstractVistaServerSideConfiguration config;

    BankingSimulatorConfigurationCustom(AbstractVistaServerSideConfiguration config) {
        this.config = config;
    }

    @Override
    public String getCardServiceSimulatorUrl() {
        return config.getConfigProperties().getValue("simulator.cardServiceSimulatorUrl",
                "http://" + "interfaces" + config.getApplicationURLNamespace(false) + "o/" + "CardServiceSimulation");
    }

    @Override
    public boolean isFundsTransferSimulationConfigurable() {
        return false;
    }

    @Override
    public File getCaledonSimulatorSftpDirectory() {
        return new File(config.vistaWorkDir(), "caledon-simulator-sftp");
    }

    @Override
    public File getBmoSimulatorSftpDirectory() {
        return new File(config.vistaWorkDir(), "bmo-simulator-sftp");
    }

    @Override
    public String getBmoSimulatorMailboxNumber() {
        return config.getConfigProperties().getValue("simulator.bmoPool.mailboxNumber", BmoInterfaceConfigurationSimulator.defaultSimulatorMailboxNumber);
    }
}
