/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.eft;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitFile;

public interface EFTTransportFacade {

    void sendFundsTransferFile(FundsTransferFile padFile) throws SftpTransportConnectionException, FileCreationException;

    FundsTransferAckFile receiveFundsTransferAcknowledgementFile(String companyId) throws SftpTransportConnectionException;

    FundsReconciliationFile receiveFundsTransferReconciliation(String companyId) throws SftpTransportConnectionException;

    /**
     * Remove the file from remote server directory in success.
     */
    void confirmReceivedFile(FundsTransferType fundsTransferType, String fileName, boolean protocolErrorFlag);

    DirectDebitFile receiveBmoFile() throws SftpTransportConnectionException;

    /**
     * Remove the file from remote server directory in success.
     */
    void confirmReceivedBmoFile(String fileName, boolean protocolErrorFlag);

}
