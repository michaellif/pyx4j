/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import static com.propertyvista.crm.server.services.reports.Util.asStubs;
import static com.propertyvista.crm.server.services.reports.Util.getSortingCriteria;
import static com.propertyvista.svg.gadgets.util.LabelHelper.makeListView;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.server.services.dashboard.gadgets.PaymentReportServiceImpl;
import com.propertyvista.crm.server.services.reports.DynamicTableTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.ReportsCommon;
import com.propertyvista.crm.server.services.reports.util.DynamicColumnWidthReportTableTemplateBuilder;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentRecordsReportModelCreator implements GadgetReportModelCreator {

    enum Params {

        TITLE, TARGET_DATE, PAYMENT_METHOD_FILTER, PAYMENT_STATUS_FILTER;

    }

    private static final I18n i18n = I18n.get(PaymentRecordsReportModelCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {

        final PaymentRecordsGadgetMetadata paymentRecordsGadgetMetadata = gadgetMetadata.duplicate(PaymentRecordsGadgetMetadata.class);
        final LogicalDate targetDate = paymentRecordsGadgetMetadata.customizeTargetDate().isBooleanTrue() ? paymentRecordsGadgetMetadata.targetDate()
                .getValue() : new LogicalDate();
        final Vector<PaymentType> paymentTypeCriteria = new Vector<PaymentType>(paymentRecordsGadgetMetadata.paymentMethodFilter().getValue());
        final Vector<PaymentRecord.PaymentStatus> paymentStatusCriteria = new Vector<PaymentRecord.PaymentStatus>(
                paymentRecordsGadgetMetadata.paymentStatusFilter());

        new PaymentReportServiceImpl().paymentRecords(new AsyncCallback<EntitySearchResult<PaymentRecordForReportDTO>>() {



            @Override
            public void onSuccess(EntitySearchResult<PaymentRecordForReportDTO> result) {//@formatter:off
                
                String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(PaymentRecordForReportDTO.class),
                                                                                   paymentRecordsGadgetMetadata)
                        .defSubTitle(Params.PAYMENT_METHOD_FILTER.name())
                        .defSubTitle(Params.PAYMENT_STATUS_FILTER.name())
                        .defSubTitle(Params.TARGET_DATE.name())
                        .build();
                
                JasperReportModel model = new DynamicTableTemplateReportModelBuilder()
                        .template(template)  
                        .data(result.getData().iterator())
                        .param(Params.TITLE.name(), paymentRecordsGadgetMetadata.getEntityMeta().getCaption())
                        .param(Params.PAYMENT_METHOD_FILTER.name(),
                               i18n.tr("Payment Method Filter: {0}", makeListView(paymentRecordsGadgetMetadata.paymentMethodFilter())))                              
                        .param(Params.PAYMENT_STATUS_FILTER.name(),
                               i18n.tr("Payment Status Filter: {0}", makeListView(paymentRecordsGadgetMetadata.paymentStatusFilter())))                              
                        .param(Params.TARGET_DATE.name(), i18n.tr("As of Date: {0}", ReportsCommon.instance().getAsOfDateFormat().format(targetDate)))
                        .build();
                        
                callback.onSuccess(model);
                
            }//@formatter:on

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }

        }, asStubs(selectedBuildings), targetDate, paymentTypeCriteria, paymentStatusCriteria, 0, 0, getSortingCriteria(paymentRecordsGadgetMetadata));
    }

}
