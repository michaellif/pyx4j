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
package com.propertyvista.config;

import java.io.File;

public abstract class BankingSimulatorConfiguration {

    public abstract String getCardServiceSimulatorUrl();

    public abstract boolean isFundsTransferSimulationConfigurable();

    public abstract File getCaledonSimulatorSftpDirectory();

    public abstract File getBmoSimulatorSftpDirectory();

    public abstract String getBmoSimulatorMailboxNumber();

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                  : ").append(getClass().getName()).append("\n");
        b.append("CardServiceSimulatorUrl             : ").append(getCardServiceSimulatorUrl()).append("\n");
        b.append("FundsTransferSimulationConfigurable : ").append(isFundsTransferSimulationConfigurable()).append("\n");
        b.append("CaledonSimulatorSftpDirectory       : ").append(getCaledonSimulatorSftpDirectory()).append("\n");
        b.append("BmoSimulatorSftpDirectory           : ").append(getBmoSimulatorSftpDirectory()).append("\n");
        b.append("BmoSimulatorMailboxNumber           : ").append(getBmoSimulatorMailboxNumber()).append("\n");
        return b.toString();
    }
}
