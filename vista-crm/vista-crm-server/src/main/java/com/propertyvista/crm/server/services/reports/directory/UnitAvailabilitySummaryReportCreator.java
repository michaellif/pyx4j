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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.server.LocalService;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.services.reports.DynamicTableTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.util.DynamicColumnWidthReportTableTemplateBuilder;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilitySummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilitySummaryReportCreator implements GadgetReportModelCreator {

    enum Params {
        TITLE, AS_OF;
    }

    private static final I18n i18n = I18n.get(UnitAvailabilitySummaryReportCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Building> buildingsFilter) {

        final UnitAvailabilitySummaryGadgetMetadata availabilitySummaryMetadata = (UnitAvailabilitySummaryGadgetMetadata) gadgetMetadata;
        final LogicalDate asOf = availabilitySummaryMetadata.customizeDate().isBooleanTrue() ? availabilitySummaryMetadata.asOf().getValue() : new LogicalDate(
                SysDateManager.getSysDate());

        LocalService.create(AvailabilityReportService.class).unitStatusSummary(new AsyncCallback<Vector<UnitAvailabilityStatusSummaryLineDTO>>() {//@formatter:off

            @Override
            public void onSuccess(Vector<UnitAvailabilityStatusSummaryLineDTO> result) {
                String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.create(UnitAvailabilityStatusSummaryLineDTO.class), availabilitySummaryMetadata)
                        .defSubTitle(Params.AS_OF.name())
                        .build();
                
                JasperReportModel reportModel = new DynamicTableTemplateReportModelBuilder()
                        .param(Params.TITLE.name(), availabilitySummaryMetadata.getEntityMeta().getCaption())
                        .param(Params.AS_OF.name(), i18n.tr("As of Date: {0}", asOf))
                        .template(template)
                        .data(result.iterator())
                        .build();
                
                callback.onSuccess(reportModel);
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }

        }, buildingsFilter, asOf);//@formatter:on
    }
}
