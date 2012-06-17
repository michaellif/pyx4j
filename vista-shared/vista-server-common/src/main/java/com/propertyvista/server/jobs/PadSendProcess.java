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

import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.biz.financial.payment.PaymentProcessFacade;

public class PadSendProcess implements PmcProcess {

    private PadFile padFile;

    @Override
    public boolean start(PmcProcessContext context) {
        padFile = ServerSideFactory.create(PaymentProcessFacade.class).preparePadFile();
        return true;
    }

    @Override
    public void executePmcJob(final PmcProcessContext context) {
        ServerSideFactory.create(PaymentProcessFacade.class).prepareEcheckPayments(context.getRunStats(), padFile);
    }

    @Override
    public void complete(PmcProcessContext context) {
        PadFile padFile = ServerSideFactory.create(PaymentProcessFacade.class).sendPadFile(this.padFile);
        if (padFile != null) {
            context.getRunStats().message().setValue("PAD file# " + padFile.fileCreationNumber().getStringView() + " created");
        } else {
            context.getRunStats().message().setValue("Nothing to send");
        }
    }

}
