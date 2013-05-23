/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-19
 * @author Amer Sohail
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.crm.rpc.dto.reports.CustomerCreditCheckReportDataDTO;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class CustomerCreditCheckReportGenerator implements ReportGenerator {

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        // TODO Auto-generated method stub
        CustomerCreditCheckReportMetadata meta = (CustomerCreditCheckReportMetadata) reportMetadata;

        List<CustomerCreditCheck> statuses = Persistence.secureQuery(createCreditCheckCriteria(meta));
        for (CustomerCreditCheck status : statuses) {
            Persistence.service().retrieveMember(status.screening());
            Persistence.service().retrieveMember(status.screening().screene());
        }
        CustomerCreditCheckReportDataDTO reportData = new CustomerCreditCheckReportDataDTO();
        reportData.unitStatuses = new Vector<CustomerCreditCheck>(statuses);

        reportData.maxAmountChecked = meta.maxAmountChecked().getValue();
        reportData.minAmountChecked = meta.minAmountChecked().getValue();
        reportData.minCreditCheckDate = meta.minCreditCheckDate().getValue();
        reportData.maxCreditCheckDate = meta.maxCreditCheckDate().getValue();

        return reportData;
    }

    private EntityQueryCriteria<CustomerCreditCheck> createCreditCheckCriteria(CustomerCreditCheckReportMetadata metadata) {
        EntityQueryCriteria<CustomerCreditCheck> criteria = EntityQueryCriteria.create(CustomerCreditCheck.class);

        if (!metadata.minCreditCheckDate().isNull()) {
            criteria.ge(criteria.proto().creditCheckDate(), metadata.minCreditCheckDate().getValue());
        }
        if (!metadata.maxCreditCheckDate().isNull()) {
            criteria.le(criteria.proto().creditCheckDate(), metadata.maxCreditCheckDate().getValue());
        }
        if (!metadata.minAmountChecked().isNull()) {
            criteria.ge(criteria.proto().amountChecked(), metadata.minAmountChecked().getValue());
        }
        if (!metadata.maxAmountChecked().isNull()) {
            criteria.le(criteria.proto().amountChecked(), metadata.maxAmountChecked().getValue());
        }
        return criteria;
    }
}
