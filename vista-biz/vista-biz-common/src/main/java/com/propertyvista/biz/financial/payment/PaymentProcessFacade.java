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
package com.propertyvista.biz.financial.payment;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;

public interface PaymentProcessFacade {

    PadFile preparePadFile();

    void prepareEcheckPayments(ExecutionMonitor executionMonitor, PadFile padFile);

    boolean sendPadFile(PadFile padFile);

    PadFile receivePadAcknowledgementFile();

    void processAcknowledgement(ExecutionMonitor executionMonitor, PadFile padFile);

    void updatePadFileAcknowledProcessingStatus(PadFile padFileId);

    PadReconciliationFile receivePadReconciliation();

    void processPadReconciliation(ExecutionMonitor executionMonitor, PadReconciliationFile reconciliationFile);

    void updatePadFileReconciliationProcessingStatus();

    void createPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate dueDate);

    void updateScheduledPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate dueDate);

    void processScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType);

}
