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
import com.propertyvista.domain.reports.PaymentRecordReportMetadata;

public class PaymentRecordReportGenerator implements ReportGenerator {

    @Override
    public Serializable generateReport(ReportMetadata metadata) {
        PaymentRecordReportMetadata reportMetadata = (PaymentRecordReportMetadata) metadata;

        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        if (!reportMetadata.from().isNull()) {
            criteria.ge(criteria.proto().updated(), reportMetadata.from());
        }
        if (!reportMetadata.until().isNull()) {
            criteria.le(criteria.proto().updated(), reportMetadata.until());
        }
        if (reportMetadata.selectBuildings().isBooleanTrue() & !reportMetadata.selectedBuildings().isEmpty()) {
            criteria.in(criteria.proto().billingAccount().lease().unit().building(), reportMetadata.selectedBuildings());
        }
        if (!reportMetadata.billingCycles().isEmpty()) {
        }

        Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>(Persistence.service().query(criteria));
        return paymentRecords;
    }

}
