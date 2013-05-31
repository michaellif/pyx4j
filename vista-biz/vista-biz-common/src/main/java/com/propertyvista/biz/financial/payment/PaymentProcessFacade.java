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

import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.operations.domain.payment.pad.PadFile;

public interface PaymentProcessFacade {

    PadFile preparePadFile();

    void prepareEcheckPayments(ExecutionMonitor executionMonitor, PadFile padFile);

    boolean sendPadFile(PadFile padFile);

    boolean receivePadAcknowledgementFile(ExecutionMonitor executionMonitor);

    void processPmcPadAcknowledgement(ExecutionMonitor executionMonitor);

    boolean receivePadReconciliation(ExecutionMonitor executionMonitor);

    void processPmcPadReconciliation(ExecutionMonitor executionMonitor);

    void createPmcPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate padGenerationDate);

    List<PaymentRecord> reportPreauthorisedPayments(LogicalDate padGenerationDate, List<Building> selectedBuildings);

    void updatePmcScheduledPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate forDate);

    void processPmcScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType);

    void verifyYardiPaymentIntegration(ExecutionMonitor executionMonitor, LogicalDate forDate);

}
