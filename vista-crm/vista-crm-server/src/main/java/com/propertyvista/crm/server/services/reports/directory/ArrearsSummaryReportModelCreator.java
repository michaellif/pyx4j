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

import static com.propertyvista.crm.server.services.reports.Util.asStubs;

import java.text.SimpleDateFormat;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.crm.server.services.dashboard.gadgets.ArrearsReportServiceImpl;
import com.propertyvista.crm.server.services.reports.DynamicTableTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.util.DynamicColumnWidthReportTableTemplateBuilder;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.financial.billing.AgingBuckets;

public class ArrearsSummaryReportModelCreator implements GadgetReportModelCreator {

    private enum Params {

        TITLE, AS_OF;

    }

    private static final I18n i18n = I18n.get(ArrearsSummaryReportModelCreator.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy");

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {
        final ArrearsSummaryGadgetMetadata arrearsSummaryGadgetMetadata = gadgetMetadata.duplicate(ArrearsSummaryGadgetMetadata.class);
        final LogicalDate asOf = arrearsSummaryGadgetMetadata.customizeDate().isBooleanTrue() ? arrearsSummaryGadgetMetadata.asOf().getValue()
                : new LogicalDate(SysDateManager.getSysDate());

        AsyncCallback<EntitySearchResult<AgingBuckets>> serviceCallback = new AsyncCallback<EntitySearchResult<AgingBuckets>>() {

            @Override
            public void onSuccess(EntitySearchResult<AgingBuckets> result) {
                //@formatter:off
                String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.create(AgingBuckets.class), arrearsSummaryGadgetMetadata)
                        .defSubTitle(Params.AS_OF.name())
                        .build();
                JasperReportModel reportModel = new DynamicTableTemplateReportModelBuilder()
                        .template(template)
                        .param(Params.TITLE.name(), arrearsSummaryGadgetMetadata.getEntityMeta().getCaption())
                        .param(Params.AS_OF.name(), i18n.tr("As of Date: {0}", DATE_FORMAT.format(asOf)))
                        .data(result.getData().iterator())
                        .build();
                callback.onSuccess(reportModel);
                //@formatter:on
            }

            @Override
            public void onFailure(Throwable arg0) {
                callback.onFailure(arg0);
            }
        };
        new ArrearsReportServiceImpl().summary(serviceCallback, asStubs(selectedBuildings), asOf);
    }

}
