/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperReportModel;

import com.propertyvista.crm.server.services.dashboard.gadgets.AvailabilityReportServiceImpl;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.StaticTemplateReportModelBuilder;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class UnitAvailabilitySummaryReportCreator implements GadgetReportModelCreator {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");

    protected static final String GRAPH = "GRAPH";

    protected static final String AS_OF = "AS_OF";

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {
        final AvailabilitySummary availabilitySummaryMetadata = (AvailabilitySummary) gadgetMetadata;
        final LogicalDate asOf = availabilitySummaryMetadata.customizeDate().isBooleanTrue() ? availabilitySummaryMetadata.asOf().getValue()
                : new LogicalDate();

        new AvailabilityReportServiceImpl().summary(new AsyncCallback<UnitAvailabilityReportSummaryDTO>() {

            @Override
            public void onSuccess(UnitAvailabilityReportSummaryDTO summary) {
                List<UnitAvailabilityReportSummaryDTO> details = new ArrayList<UnitAvailabilityReportSummaryDTO>();
                details.add(summary);
                callback.onSuccess(new StaticTemplateReportModelBuilder(AvailabilitySummary.class).param(AS_OF, DATE_FORMAT.format(asOf))
                        .data(details.iterator()).build());
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }

        }, new Vector<Key>(selectedBuildings), asOf);
    }
}
