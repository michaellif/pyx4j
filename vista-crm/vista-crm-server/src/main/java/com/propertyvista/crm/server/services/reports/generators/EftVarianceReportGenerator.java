/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Vector;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDTO;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDetailsDTO;
import com.propertyvista.domain.reports.EftVarianceReportMetadata;

public class EftVarianceReportGenerator implements ReportGenerator {

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        EftVarianceReportMetadata eftVarianceReportMetadata = (EftVarianceReportMetadata) reportMetadata;

        Vector<EftVarianceReportRecordDTO> varianceReportRecord = new Vector<EftVarianceReportRecordDTO>();
        // ADD some mockup

        EftVarianceReportRecordDTO record = EntityFactory.create(EftVarianceReportRecordDTO.class);
        record.building().setValue("bath1234");
        record.unit().setValue("101");
        record.leaseId().setValue("t000012356");

        {
            EftVarianceReportRecordDetailsDTO details = record.details().$();
            details.tenantName().setValue("Peter Petechkyin");
            details.paymentMethod().setValue("blalba");
            details.totalEft().setValue(new BigDecimal("1000.5"));
            details.charges().setValue(new BigDecimal("500.00"));
            details.difference().setValue(new BigDecimal("500.5"));
            record.details().add(details);
        }

        {
            EftVarianceReportRecordDetailsDTO details = record.details().$();
            details.tenantName().setValue("Vasya Vasechkin");
            details.paymentMethod().setValue("12355adf5551");
            details.totalEft().setValue(new BigDecimal("2000.5"));
            details.charges().setValue(new BigDecimal("1000.00"));
            details.difference().setValue(new BigDecimal("1000.5"));
            record.details().add(details);
        }
        record.leaseTotals().totalEft().setValue(new BigDecimal("3001"));
        record.leaseTotals().charges().setValue(new BigDecimal("1500"));
        record.leaseTotals().difference().setValue(new BigDecimal("1501"));
        varianceReportRecord.add(record);

        return varianceReportRecord;
    }

    @Override
    public ReportProgressStatus getProgressStatus() {
        // TODO 
        return null;
    }

    @Override
    public void abort() {
        // TODO 
    }

}
