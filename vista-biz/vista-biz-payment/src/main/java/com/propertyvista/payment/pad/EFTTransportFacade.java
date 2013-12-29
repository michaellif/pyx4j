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
package com.propertyvista.payment.pad;

import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitFile;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.data.PadAckFile;
import com.propertyvista.server.sftp.SftpTransportConnectionException;

public interface EFTTransportFacade {

    void sendPadFile(PadFile padFile) throws SftpTransportConnectionException, FileCreationException;

    PadAckFile receivePadAcknowledgementFile(String companyId) throws SftpTransportConnectionException;

    PadReconciliationFile receivePadReconciliation(String companyId) throws SftpTransportConnectionException;

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
