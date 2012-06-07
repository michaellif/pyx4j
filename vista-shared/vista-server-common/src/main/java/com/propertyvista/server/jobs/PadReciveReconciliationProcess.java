/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.admin.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.biz.financial.payment.PaymentProcessFacade;

public class PadReciveReconciliationProcess implements PmcProcess {

    private PadReconciliationFile reconciliationFile;

    @Override
    public boolean start() {
        reconciliationFile = ServerSideFactory.create(PaymentProcessFacade.class).recivePadReconciliation();
        return (reconciliationFile != null);
    }

    @Override
    public void executePmcJob() {
        ServerSideFactory.create(PaymentProcessFacade.class).processPadReconciliation(PmcProcessContext.getRunStats(), reconciliationFile);
    }

    @Override
    public void complete() {
        ServerSideFactory.create(PaymentProcessFacade.class).updatePadFilesProcessingStatus();
    }

}
