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
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

import com.propertyvista.biz.financial.payment.PaymentReportFacade;
import com.propertyvista.biz.financial.payment.PreauthorizedPaymentsReportCriteria;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDTO;
import com.propertyvista.crm.server.services.reports.util.ReportProgressStatusHolderExectutionMonitorAdapter;
import com.propertyvista.crm.server.util.BuildingsCriteriaNormalizer;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.reports.EftVarianceReportMetadata;

public class EftVarianceReportGenerator implements ReportGenerator {

    private static final I18n i18n = I18n.get(EftVarianceReportGenerator.class);

    private final BuildingsCriteriaNormalizer buildingCriteriaNormalizer;

    private volatile ReportProgressStatusHolderExectutionMonitorAdapter reportProgressStatusHolder;

    public EftVarianceReportGenerator() {
        buildingCriteriaNormalizer = new BuildingsCriteriaNormalizer(EntityFactory.getEntityPrototype(PaymentRecord.class).billingAccount().lease().unit()
                .building());
    }

    @Override
    public Serializable generateReport(ReportTemplate metadata) {
        reportProgressStatusHolder = new ReportProgressStatusHolderExectutionMonitorAdapter();

        EftVarianceReportMetadata reportMetadata = (EftVarianceReportMetadata) metadata;
        Vector<EftVarianceReportRecordDTO> varianceReportRecord = new Vector<EftVarianceReportRecordDTO>();

        List<Building> selectedBuildings = buildingCriteriaNormalizer.normalize(//
                reportMetadata.filterByPortfolio().getValue(false) ? reportMetadata.selectedPortfolios() : null, //
                reportMetadata.filterByBuildings().getValue(false) ? reportMetadata.selectedBuildings() : null);

        // Find PadGenerationDate for each BillingCycle in system, they may be different
        Set<LogicalDate> padGenerationDays = new HashSet<LogicalDate>();
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);

        if (selectedBuildings != null) {
            criteria.in(criteria.proto().building(), selectedBuildings);
        }
        criteria.eq(criteria.proto().billingCycleStartDate(), reportMetadata.billingCycleStartDate());
        criteria.isNull(criteria.proto().actualAutopayExecutionDate());

        for (BillingCycle cycle : Persistence.secureQuery(criteria)) {
            if (reportProgressStatusHolder.isTerminationRequested()) {
                break;
            }
            padGenerationDays.add(cycle.targetAutopayExecutionDate().getValue());
        }

        int progress = 0;
        int count = padGenerationDays.size();
        for (LogicalDate padGenerationDate : padGenerationDays) {
            if (reportProgressStatusHolder.isTerminationRequested()) {
                break;
            }
            PreauthorizedPaymentsReportCriteria reportCriteria = new PreauthorizedPaymentsReportCriteria(padGenerationDate, selectedBuildings);
            varianceReportRecord.addAll(ServerSideFactory.create(PaymentReportFacade.class).reportEftVariance(reportCriteria));
            reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 1, 1, progress++, count));
        }
        return varianceReportRecord;
    }

    @Override
    public ReportProgressStatus getProgressStatus() {
        return reportProgressStatusHolder.get();
    }

    @Override
    public void abort() {
        reportProgressStatusHolder.requestTermination();
    }

}
