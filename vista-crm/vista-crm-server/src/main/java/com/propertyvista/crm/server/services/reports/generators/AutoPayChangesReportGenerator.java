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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.services.reports.ReportExporter;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.biz.financial.payment.PaymentReportFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.payment.AutoPayReviewDTO;

public class AutoPayChangesReportGenerator implements ReportGenerator, ReportExporter {

    @Override
    public ReportProgressStatus getProgressStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable generateReport(ReportMetadata reportMetadata) {
        List<Building> selectedBuildings = Persistence.secureQuery(EntityQueryCriteria.create(Building.class));
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
        // TODO Auto-generated method stub
        return null;
    }

}
