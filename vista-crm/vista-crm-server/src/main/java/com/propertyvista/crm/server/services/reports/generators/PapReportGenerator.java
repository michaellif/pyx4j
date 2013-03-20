/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.util.Vector;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.site.server.services.reports.ReportGenerator;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.reports.PapReportMetadata;

public class PapReportGenerator implements ReportGenerator {

    @Override
    public Serializable generateReport(ReportMetadata metadata) {
        PapReportMetadata reportMetadata = (PapReportMetadata) metadata;
        EntityQueryCriteria<PaymentRecord> criteria = makeCriteria(reportMetadata);
        Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>(Persistence.service().query(criteria));
        return paymentRecords;
    }

    private EntityQueryCriteria<PaymentRecord> makeCriteria(PapReportMetadata reportMetadata) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.desc(criteria.proto().padBillingCycle().billingType());
        criteria.desc(criteria.proto().padBillingCycle().billingCycleStartDate());

        criteria.ne(criteria.proto().preauthorizedPayment().isDeleted(), true);
        criteria.isNotNull(criteria.proto().padBillingCycle());

        if (reportMetadata.filterByTargetDate().isBooleanTrue()) {
            if (!reportMetadata.from().isNull()) {
                criteria.ge(criteria.proto().targetDate(), reportMetadata.from());
            }
            if (!reportMetadata.until().isNull()) {
                criteria.le(criteria.proto().targetDate(), reportMetadata.until());
            }
        }
        if (reportMetadata.filterByBuildings().isBooleanTrue()) {
            if (!reportMetadata.selectedBuildings().isEmpty()) {
                criteria.in(criteria.proto().billingAccount().lease().unit().building(), reportMetadata.selectedBuildings());
            } else {
                criteria.isNull(criteria.proto().billingAccount().lease().unit().building());
            }
        }

        return criteria;
    }

}
