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
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.services.reports.ReportExporter;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.biz.financial.payment.PaymentReportFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.dto.payment.AutoPayReviewDTO;

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
            EntityQueryCriteria<Building> buildingsCriteria = EntityQueryCriteria.create(Building.class);
            buildingsCriteria.in(buildingsCriteria.proto().id(), buildingKeys);
            selectedBuildings = Persistence.secureQuery(buildingsCriteria, AttachLevel.IdOnly);
        } else {
            selectedBuildings = Persistence.secureQuery(EntityQueryCriteria.create(Building.class));
        }

        Vector<AutoPayReviewDTO> suspenedPreauthorizedPayments = new Vector<AutoPayReviewDTO>(ServerSideFactory.create(PaymentReportFacade.class)
                .reportSuspendedPreauthorizedPayments(selectedBuildings));

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

}
