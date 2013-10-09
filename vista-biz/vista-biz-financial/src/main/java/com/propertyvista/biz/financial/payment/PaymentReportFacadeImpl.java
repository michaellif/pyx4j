/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDTO;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.dto.payment.AutoPayReviewDTO;

public class PaymentReportFacadeImpl implements PaymentReportFacade {

    @Override
    public List<AutoPayReviewDTO> reportPreauthorizedPaymentsRequiredReview(PreauthorizedPaymentsReportCriteria reportCriteria) {
        return new PreauthorizedPaymentAutoPayReviewReport().reportPreauthorizedPaymentsRequiredReview(reportCriteria);
    }

    @Override
    public List<PaymentRecord> reportPreauthorisedPayments(PreauthorizedPaymentsReportCriteria reportCriteria, ExecutionMonitor executionMonitor) {
        return new PreauthorizedPaymentsManager().reportPreauthorisedPayments(reportCriteria, executionMonitor);
    }

    @Override
    public List<EftVarianceReportRecordDTO> reportEftVariance(PreauthorizedPaymentsReportCriteria reportCriteria) {
        return new PaymentReportEftVariance().reportEftVariance(reportCriteria);
    }

}
