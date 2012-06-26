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
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.crm.server.services.dashboard.gadgets.AvailabilityReportServiceImpl;
import com.propertyvista.crm.server.services.reports.DynamicTableTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.ReportsCommon;
import com.propertyvista.crm.server.services.reports.util.DynamicColumnWidthReportTableTemplateBuilder;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMeta;

public class UnitAvailabilityStatusReportCreator implements GadgetReportModelCreator {

    private enum ReportParams {

        TITLE, AS_OF, FILTER_CRITERIA;

    }

    private final static I18n i18n = I18n.get(UnitAvailabilityStatusReportCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {

        final UnitAvailabilityGadgetMeta metadata = gadgetMetadata.duplicate(UnitAvailabilityGadgetMeta.class);
        final LogicalDate asOf = metadata.customizeDate().isBooleanTrue() ? metadata.asOf().getValue() : new LogicalDate(SysDateManager.getSysDate());

        new AvailabilityReportServiceImpl().unitStatusList(new AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>>() {






            @Override
            public void onSuccess(EntitySearchResult<UnitAvailabilityStatus> result) {                
                //@formatter:off
                String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class), metadata)
                        .defSubTitle(ReportParams.FILTER_CRITERIA.name())
                        .defSubTitle(ReportParams.AS_OF.name())
                        .build();
                //@formatter:on

                callback.onSuccess(new DynamicTableTemplateReportModelBuilder()//@formatter:off
                        .template(template)
                        .param(ReportParams.TITLE.name(), i18n.tr("Unit Availability Status"))
                        .param(ReportParams.FILTER_CRITERIA.name(), i18n.tr("Filter Setting: {0}", metadata.filterPreset().getValue().toString()))
                        .param(ReportParams.AS_OF.name(), i18n.tr("As of Date: {0}", ReportsCommon.instance().getAsOfDateFormat().format(asOf)))
                        .data(result.getData().iterator())
                        .build());//@formatter:on
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

        }, new Vector<Key>(selectedBuildings), metadata.filterPreset().getValue(), asOf, new Vector<Sort>(), 0, Integer.MAX_VALUE);
    }
}
