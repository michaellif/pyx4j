/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad.mock;

import com.propertyvista.operations.domain.payment.pad.FundsTransferType;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.EFTTransportFacade;
import com.propertyvista.payment.pad.data.PadAckFile;

public class EFTTransportFacadeMock implements EFTTransportFacade {

    public static void init() {
        // Initialize listeners
        EFTBankMock.instance();
    }

    @Override
    public void sendPadFile(PadFile padFile) {
        EFTBankMock.instance().receivedPadFile(padFile);
    }

    @Override
    public PadAckFile receivePadAcknowledgementFile(String companyId) {
        return EFTBankMock.instance().acknowledgeFile(companyId);
    }

    @Override
    public PadReconciliationFile receivePadReconciliation(String companyId) {
        return EFTBankMock.instance().reconciliationFile(companyId);
    }

    @Override
    public void confirmReceivedFile(FundsTransferType fundsTransferType, String fileName, boolean protocolErrorFlag) {
    }

}
