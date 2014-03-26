/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.text.SimpleDateFormat;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsStatusReportModelCreator implements GadgetReportModelCreator {

    private enum Param {

        TITLE, ARREARS_CATEGORY, AS_OF;

    }

    private final static I18n i18n = I18n.get(ArrearsStatusReportModelCreator.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy");

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Building> buildingsFilter) {
        callback.onFailure(new Error("not implemented"));
//        final ArrearsStatusGadgetMetadata metadata = gadgetMetadata.duplicate(ArrearsStatusGadgetMetadata.class);
//        final LogicalDate asOf = metadata.customizeDate().getValue(false) ? metadata.asOf().getValue() : new LogicalDate(SysDateManager.getSysDate());
//        final DebitType arrearsCategory = metadata.category().getValue();
//        final Vector<Sort> sortingCriteria = new Vector<Sort>();
//        if (!metadata.primarySortColumn().isNull()) {
//            sortingCriteria.add(new Sort(metadata.primarySortColumn().propertyPath().getValue(), !metadata.sortAscending().getValue(false)));
//        }
//
//        AsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>> serviceCallback = new AsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>>() {
//
//            @Override
//            public void onSuccess(EntitySearchResult<LeaseArrearsSnapshotDTO> result) {
//                //@formatter:off
//                String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.create(LeaseArrearsSnapshotDTO.class), metadata)
//                        .defSubTitle(Param.ARREARS_CATEGORY.name())
//                        .defSubTitle(Param.AS_OF.name())
//                        .build();
//                
//                JasperReportModel model = new DynamicTableTemplateReportModelBuilder()
//                        .template(template)
//                        .param(Param.TITLE.name(), metadata.getEntityMeta().getCaption())
//                        .param(Param.ARREARS_CATEGORY.name(), i18n.tr("Arrears Category: {0}", arrearsCategory.toString()))
//                        .param(Param.AS_OF.name(), i18n.tr("As of Date: {0}", DATE_FORMAT.format(asOf)))
//                        .data(result.getData().iterator())
//                        .build();
//                //@formatter:on
//                callback.onSuccess(model);
//
//            }
//
//            @Override
//            public void onFailure(Throwable arg0) {
//                callback.onFailure(arg0);
//            }
//
//        };
//
//        new ArrearsReportServiceImpl().leaseArrearsRoster(//@formatter:off
//                serviceCallback,
//                buildingsFilter,
//                asOf,
//                arrearsCategory,
//                sortingCriteria,
//                0,
//                Integer.MAX_VALUE);//@formatter:on
    }
}
