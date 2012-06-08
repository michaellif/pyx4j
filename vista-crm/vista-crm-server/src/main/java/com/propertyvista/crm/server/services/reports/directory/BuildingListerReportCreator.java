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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.server.services.building.BuildingCrudServiceImpl;
import com.propertyvista.crm.server.services.reports.DynamicTableTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.util.DynamicColumnWidthReportTableTemplateBuilder;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerReportCreator implements GadgetReportModelCreator {

    enum Params {

        TITLE

    }

    private final static I18n i18n = I18n.get(BuildingListerReportCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {
        final BuildingLister metadata = gadgetMetadata.duplicate(BuildingLister.class);

        new BuildingCrudServiceImpl().list(new AsyncCallback<EntitySearchResult<BuildingDTO>>() {

            @Override
            public void onSuccess(EntitySearchResult<BuildingDTO> result) {
                String reportTemplate = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(BuildingDTO.class), metadata).build();
                JasperReportModel reportModel = new DynamicTableTemplateReportModelBuilder()//@formatter:off
                        .template(reportTemplate)
                        .param(Params.TITLE.name(), i18n.tr("Building Details"))
                        .data(result.getData().iterator())
                        .build();//@formatter:on

                callback.onSuccess(reportModel);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

        }, getSearchCriteria(metadata));
    }

    private EntityListCriteria<BuildingDTO> getSearchCriteria(BuildingLister metadata) {
        EntityListCriteria<BuildingDTO> criteria = new EntityListCriteria<BuildingDTO>(BuildingDTO.class);
        String sortCoulmn = metadata.primarySortColumn().propertyPath().getValue();
        if (sortCoulmn != null) {
            criteria.sort(new Sort(sortCoulmn, !metadata.sortAscending().isBooleanTrue()));
        }
        return criteria;
    }

}
