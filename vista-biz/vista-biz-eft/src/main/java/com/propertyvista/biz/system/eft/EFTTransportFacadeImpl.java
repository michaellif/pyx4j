/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.eft;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.eft.caledoneft.CaledonFundsTransferManager;
import com.propertyvista.eft.dbp.BmoManager;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitFile;

public class EFTTransportFacadeImpl implements EFTTransportFacade {

    @Override
    public void sendFundsTransferFile(FundsTransferFile padFile) throws SftpTransportConnectionException, FileCreationException {
        new CaledonFundsTransferManager().sendFundsTransferFile(padFile);
    }

    @Override
    public FundsTransferAckFile receiveFundsTransferAcknowledgementFile(String companyId) throws SftpTransportConnectionException {
        return new CaledonFundsTransferManager().receiveFundsTransferAcknowledgementFile(companyId);
    }

    @Override
    public FundsReconciliationFile receiveFundsTransferReconciliation(String companyId) throws SftpTransportConnectionException {
        return new CaledonFundsTransferManager().receiveFundsTransferReconciliation(companyId);
    }

    @Override
    public void confirmReceivedFile(FundsTransferType fundsTransferType, String fileName, boolean protocolErrorFlag) {
        new CaledonFundsTransferManager().confirmReceivedFile(fundsTransferType, fileName, protocolErrorFlag);
    }

    @Override
    public DirectDebitFile receiveBmoFile() throws SftpTransportConnectionException {
        return new BmoManager().receiveBmoFile();
    }

    @Override
    public void confirmReceivedBmoFile(String fileName, boolean protocolErrorFlag) {
        new BmoManager().confirmReceivedBmoFile(fileName, protocolErrorFlag);
    }

}
