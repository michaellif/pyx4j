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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.operations.domain.payment.pad.PadFile;

public class PadReceiveAcknowledgmentProcess implements PmcProcess {

    private static final String EXECUTION_MONITOR_SECTION_NAME = "PadAcknowledgementReceived";

    private PadFile padFile;

    @Override
    public boolean start(PmcProcessContext context) {
        if (VistaDeployment.isVistaStaging()) {
            return false;
        }
        padFile = ServerSideFactory.create(PaymentProcessFacade.class).receivePadAcknowledgementFile();
        if (padFile != null) {
            if (!padFile.acknowledgmentRejectReasonMessage().isNull()) {

            }
            if (padFile.status().getValue() != PadFile.PadFileStatus.Acknowledged) {
                if (!padFile.acknowledgmentRejectReasonMessage().isNull()) {
                    context.getExecutionMonitor().addFailedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME,
                            SimpleMessageFormat.format("Pad file was rejected, {0}", padFile.acknowledgmentRejectReasonMessage().getValue())
                    );//@formatter:on);
                    throw new Error(padFile.acknowledgmentRejectReasonMessage().getValue());
                } else {
                    context.getExecutionMonitor().addFailedEvent(//@formatter:off
                            EXECUTION_MONITOR_SECTION_NAME,
                            SimpleMessageFormat.format("Pad file acknowledgment failed. File AcknowledgmentStatus {0}; File status {1}.", padFile.acknowledgmentStatus().getValue(), padFile.status().getValue())
                    );//@formatter:on);
                    throw new Error("Pad file acknowledgment failed. File AcknowledgmentStatus '" + padFile.acknowledgmentStatus().getValue()
                            + "'; File status '" + padFile.status().getValue() + "'");
                }
            }
            context.getExecutionMonitor().addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME);
        }
        return (padFile != null);
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(PaymentProcessFacade.class).processAcknowledgement(context.getExecutionMonitor(), padFile);
    }

    @Override
    public void complete(PmcProcessContext context) {
        ServerSideFactory.create(PaymentProcessFacade.class).updatePadFileAcknowledProcessingStatus(padFile);
    }

}
