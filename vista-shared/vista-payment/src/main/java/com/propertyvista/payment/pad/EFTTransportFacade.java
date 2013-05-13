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

import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.data.PadAckFile;

public interface EFTTransportFacade {

    public void sendPadFile(PadFile padFile);

    public PadAckFile receivePadAcknowledgementFile(String companyId) throws EFTTransportConnectionException;

    public PadReconciliationFile receivePadReconciliation(String companyId) throws EFTTransportConnectionException;

    /**
     * Remove the file from remote server directory in success.
     */
    public void confirmReceivedFile(String fileName, boolean protocolErrorFlag);
}
