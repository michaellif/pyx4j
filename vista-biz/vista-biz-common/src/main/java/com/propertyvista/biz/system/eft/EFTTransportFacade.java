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
 */
package com.propertyvista.biz.system.eft;

import java.util.Collection;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.domain.financial.CaledonFundsTransferType;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationTO;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportTO;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitFile;

public interface EFTTransportFacade {

    // CaledonEFT

    void sendFundsTransferFile(FundsTransferFile padFile) throws SftpTransportConnectionException, FileCreationException;

    FundsTransferAckFile receiveFundsTransferAcknowledgementFile(String companyId) throws SftpTransportConnectionException;

    FundsReconciliationFile receiveFundsTransferReconciliation(String companyId) throws SftpTransportConnectionException;

    /**
     * Remove the file from remote server directory in success.
     */
    void confirmReceivedFile(CaledonFundsTransferType fundsTransferType, String fileName, boolean protocolErrorFlag);

    // CaledonCards

    CardsReconciliationTO receiveCardsReconciliationFiles(String cardsReconciliationId) throws SftpTransportConnectionException;

    DailyReportTO receiveCardsDailyReportFile(String cardsReconciliationId) throws SftpTransportConnectionException;

    void confirmReceivedCardsReconciliationFiles(Collection<String> fileNames, boolean protocolErrorFlag);

    // BMO

    DirectDebitFile receiveBmoFile() throws SftpTransportConnectionException;

    /**
     * Remove the file from remote server directory in success.
     */
    void confirmReceivedBmoFile(String fileName, boolean protocolErrorFlag);

}
