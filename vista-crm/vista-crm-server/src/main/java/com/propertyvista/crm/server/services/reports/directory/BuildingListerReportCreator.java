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
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingListerReportCreator implements GadgetReportModelCreator {

    enum Params {

        TITLE

    }

    private final static I18n i18n = I18n.get(BuildingListerReportCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Building> buildingsFilter) {
        final BuildingListerGadgetMetadata metadata = gadgetMetadata.duplicate(BuildingListerGadgetMetadata.class);
        callback.onFailure(new Error("not implemented"));
//        LocalService.create(BuildingCrudService.class).list(new AsyncCallback<EntitySearchResult<BuildingDTO>>() {
//
//            @Override
//            public void onSuccess(EntitySearchResult<BuildingDTO> result) {
//                String reportTemplate = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(BuildingDTO.class), metadata).build();
//                JasperReportModel reportModel = new DynamicTableTemplateReportModelBuilder()//@formatter:off
//                        .template(reportTemplate)
//                        .param(Params.TITLE.name(), i18n.tr("Building Details"))
//                        .data(result.getData().iterator())
//                        .build();//@formatter:on
//
//                callback.onSuccess(reportModel);
//
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                callback.onFailure(caught);
//            }
//
//        }, getSearchCriteria(metadata));
    }

}
