/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilityStatusReportCreator implements GadgetReportModelCreator {

    private enum ReportParams {

        TITLE, AS_OF, FILTER_CRITERIA;

    }

    private final static I18n i18n = I18n.get(UnitAvailabilityStatusReportCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Building> selectedBuildings) {
        final UnitAvailabilityGadgetMetadata metadata = gadgetMetadata.duplicate(UnitAvailabilityGadgetMetadata.class);
        callback.onFailure(new Error("not implemented"));
//        final LogicalDate asOf = metadata.customizeDate().getValue(false) ? metadata.asOf().getValue() : new LogicalDate(SysDateManager.getSysDate());
//
//        LocalService.create(UnitAvailabilityGadgetService.class).unitAvailabilityStatusList(new AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>>() {
//
//            @Override
//            public void onSuccess(EntitySearchResult<UnitAvailabilityStatus> result) {//@formatter:off
//     
//                String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class), metadata)
//                        .defSubTitle(ReportParams.FILTER_CRITERIA.name())
//                        .defSubTitle(ReportParams.AS_OF.name())
//                        .build();

//                callback.onSuccess(new DynamicTableTemplateReportModelBuilder()
//                        .template(template)
//                        .param(ReportParams.TITLE.name(), i18n.tr("Unit Availability Status"))
//                        .param(ReportParams.FILTER_CRITERIA.name(), i18n.tr("Filter Setting: {0}", metadata.filterPreset().getValue().toString()))
//                        .param(ReportParams.AS_OF.name(), i18n.tr("As of Date: {0}", ReportsCommon.instance().getAsOfDateFormat().format(asOf)))
//                        .data(result.getData().iterator())
//                        .build());
//            }//@formatter:on
//
//            @Override
//            public void onFailure(Throwable caught) {
//                callback.onFailure(caught);
//            }
//
//        }, selectedBuildings, metadata.filterPreset().getValue(), asOf, new Vector<Sort>(), 0, Integer.MAX_VALUE);
    }
}
