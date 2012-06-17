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

import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.admin.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.payment.PaymentType;

public interface PaymentProcessFacade {

    PadFile preparePadFile();

    void prepareEcheckPayments(StatisticsRecord dynamicStatisticsRecord, PadFile padFile);

    PadFile sendPadFile(PadFile padFile);

    PadFile recivePadAcknowledgementFile();

    void processAcknowledgement(StatisticsRecord dynamicStatisticsRecord, PadFile padFile);

    PadReconciliationFile recivePadReconciliation();

    void processPadReconciliation(StatisticsRecord dynamicStatisticsRecord, PadReconciliationFile reconciliationFile);

    void updatePadFilesProcessingStatus();

    void createPreauthorisedPayments(StatisticsRecord dynamicStatisticsRecord, LogicalDate dueDate);

    void processScheduledPayments(StatisticsRecord dynamicStatisticsRecord, PaymentType paymentType);

}
