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

public interface PaymentProcessFacade {

    PadFile preparePadFile();

    void prepareEcheckPayments(ExecutionMonitor executionMonitor, PadFile padFile);

    boolean sendPadFile(PadFile padFile);

    boolean receivePadAcknowledgementFile(ExecutionMonitor executionMonitor);

    void processPmcPadAcknowledgement(ExecutionMonitor executionMonitor);

    boolean receivePadReconciliation(ExecutionMonitor executionMonitor);

    void processPmcPadReconciliation(ExecutionMonitor executionMonitor);

    void createPmcPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate dueDate);

    void updatePmcScheduledPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate dueDate);

    void processPmcScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType);

}
