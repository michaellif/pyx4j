/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.services.reports.ReportExporter;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.biz.financial.payment.PaymentReportFacade;
import com.propertyvista.biz.financial.payment.PreauthorizedPaymentsReportCriteria;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayChangesReportGenerator implements ReportGenerator, ReportExporter {

    private final ReportProgressStatusHolder reportProgressStatusHolder;

    public AutoPayChangesReportGenerator() {
        reportProgressStatusHolder = new ReportProgressStatusHolder();
    }

    @Override
    public ReportProgressStatus getProgressStatus() {
        return reportProgressStatusHolder.get();
    }

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        AutoPayChangesReportMetadata autoPayChangesReportMetadata = (AutoPayChangesReportMetadata) reportMetadata;
        // query buildings to enforce portfolio:        
        List<Building> selectedBuildings = null;

        if (!autoPayChangesReportMetadata.buildings().isEmpty()) {
            Vector<Key> buildingKeys = new Vector<Key>(autoPayChangesReportMetadata.buildings().size());
            for (Building b : autoPayChangesReportMetadata.buildings()) {
                buildingKeys.add(b.getPrimaryKey());
            }
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.in(criteria.proto().id(), buildingKeys);
            selectedBuildings = Persistence.secureQuery(criteria, AttachLevel.IdOnly);
        } else {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.eq(criteria.proto().suspended(), false);
            selectedBuildings = Persistence.secureQuery(criteria);
        }

        PreauthorizedPaymentsReportCriteria reportCriteria = new PreauthorizedPaymentsReportCriteria(null, selectedBuildings);
        if (autoPayChangesReportMetadata.filterByExpectedMoveOut().isBooleanTrue()) {
            reportCriteria.setExpectedMoveOutCriteris(autoPayChangesReportMetadata.minimum().getValue(), autoPayChangesReportMetadata.maximum().getValue());
        }
        reportCriteria.setLeasesOnNoticeOnly(autoPayChangesReportMetadata.leasesOnNoticeOnly().isBooleanTrue());

        Vector<AutoPayReviewDTO> suspenedPreauthorizedPayments = new Vector<AutoPayReviewDTO>(ServerSideFactory.create(PaymentReportFacade.class)
                .reportPreauthorizedPaymentsRequiredReview(reportCriteria));

        if (false) {
            devFillWithMockup(suspenedPreauthorizedPayments);
        }
        return suspenedPreauthorizedPayments;
    }

    @Override
    public void abort() {
        // TODO Auto-generated method stub
    }

    @Override
    public ExportedReport export(Serializable report) {
        @SuppressWarnings("unchecked")
        Vector<AutoPayReviewDTO> records = (Vector<AutoPayReviewDTO>) report;
        return new AutoPayChangesReportExport().createReport(records, reportProgressStatusHolder);
    }

    /**
     * Generate mockup data for UI debugging
     */
    private void devFillWithMockup(Vector<AutoPayReviewDTO> data) {
        if (!ApplicationMode.isDevelopment()) {
            return;
        }
        AutoPayReviewDTO dto = EntityFactory.create(AutoPayReviewDTO.class);
        dto.building().setValue("building #B");
        dto.unit().setValue("unit #U");
        dto.leaseId().setValue("t000000000");
        Lease lease = EntityFactory.create(Lease.class);
        lease.setPrimaryKey(new Key(1));
        dto.lease().set(lease);
        dto.paymentDue().setValue(new LogicalDate());
        AutoPayReviewPreauthorizedPaymentDTO papDto = dto.pap().$();
        papDto.tenantName().setValue("Ivan Vasilyevich");
        AutoPayReviewChargeDTO papItemDto = papDto.items().$();
        papItemDto.leaseCharge().setValue("ccode");
        papItemDto.suspended().totalPrice().setValue(new BigDecimal("1000"));
        papItemDto.suspended().percentChange().setValue(new BigDecimal("0.1"));
        papItemDto.suspended().payment().setValue(new BigDecimal("1200"));
        papItemDto.suspended().percent().setValue(new BigDecimal("0.25"));
        papItemDto.suggested().totalPrice().setValue(new BigDecimal("1200"));
        papItemDto.suggested().percentChange().setValue(new BigDecimal("0.2"));
        papItemDto.suggested().payment().setValue(new BigDecimal("1234"));
        papItemDto.suggested().percent().setValue(new BigDecimal("0.11"));
        papDto.items().add(papItemDto);

        for (int i = 0; i < 3; ++i) {
            dto.pap().add(papDto);
        }
        for (int i = 0; i < 1000; ++i) {
            data.add(dto);
        }

    }

}
