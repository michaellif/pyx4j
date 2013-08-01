/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.directory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentsSummaryReportModelCreator implements GadgetReportModelCreator {

    enum Params {

        TITLE, PAYMENT_STATUS_FILTER, PAYMENT_FEES, AS_OF

    }

    private static final I18n i18n = I18n.get(PaymentsSummaryReportModelCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Building> selectedBuildings) {
        callback.onFailure(new Error("not implemented"));
//        final PaymentsSummaryGadgetMetadata paymentsSummaryGadgetMetadata = gadgetMetadata.duplicate(PaymentsSummaryGadgetMetadata.class);
//        final LogicalDate targetDate = paymentsSummaryGadgetMetadata.customizeDate().isBooleanTrue() ? paymentsSummaryGadgetMetadata.asOf().getValue()
//                : new LogicalDate(SysDateManager.getSysDate());
//        final Vector<PaymentRecord.PaymentStatus> paymentStatusCriteria = new Vector<PaymentRecord.PaymentStatus>(paymentsSummaryGadgetMetadata.paymentStatus()
//                .getValue());
//        final Vector<PaymentFeesDTO> paymentFees = new Vector<PaymentFeesDTO>();
//
//        LocalService.create(PaymentReportService.class).paymentsFees(//@formatter:off
//                new AsyncCallback<Vector<PaymentFeesDTO>>() {
//                    
//                    @Override
//                    public void onSuccess(Vector<PaymentFeesDTO> result) {
//                            paymentFees.addAll(result);
//                    }
//                    
//                    @Override
//                    public void onFailure(Throwable error) {
//                        throw new RuntimeException(error);
//                    }
//
//                }
//        );//@formatter:on
//
//        LocalService.create(PaymentReportService.class).paymentsSummary(//@formatter:off                
//                new AsyncCallback<EntitySearchResult<PaymentsSummary>>() {
//
//                    @Override
//                    public void onSuccess(EntitySearchResult<PaymentsSummary> result) {
//                        String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(PaymentsSummary.class), paymentsSummaryGadgetMetadata)                                
//                                .defSubTitle(Params.AS_OF.name())
//                                .defSubTitle(Params.PAYMENT_STATUS_FILTER.name())
//                                .defSubTitle(Params.PAYMENT_FEES.name())
//                                .build();
//                        
//                        DynamicTableTemplateReportModelBuilder builder = new DynamicTableTemplateReportModelBuilder()
//                                .template(template)
//                                .data(result.getData().iterator())
//                                .param(Params.TITLE.name(), paymentsSummaryGadgetMetadata.getEntityMeta().getCaption())
//                                .param(Params.AS_OF.name(), i18n.tr("As of Date: {0}", ReportsCommon.instance().getAsOfDateFormat().format(targetDate)))
//                                .param(Params.PAYMENT_STATUS_FILTER.name(), i18n.tr("Payment Status Filter: {0}", LabelHelper.makeListView(paymentStatusCriteria)));
//                        
//                        if (!paymentFees.isEmpty()) {
//                            builder.param(Params.PAYMENT_FEES.name(), i18n.tr("The following payment fees are applied (per transaction): {0}", makePaymentFeesLabel(paymentFees)));
//                        } else {
//                            builder.param(Params.PAYMENT_FEES.name(), "");
//                        }
//                        
//                        JasperReportModel model = builder.build();
//                        callback.onSuccess(model);
//                    }
//                    
//                    @Override
//                    public void onFailure(Throwable error) {
//                        callback.onFailure(error);
//                    }
//                },
//                selectedBuildings,
//                targetDate,
//                paymentStatusCriteria,
//                0,
//                -1,
//                getSortingCriteria(paymentsSummaryGadgetMetadata)
//        );//@formatter:on

    }

    private String makePaymentFeesLabel(Vector<PaymentFeesDTO> paymentFees) {

        Vector<String> labels = new Vector<String>();
        labels.addAll(makePaymentFeeLabelList(paymentFees.get(0)));
        labels.addAll(makePaymentFeeLabelList(paymentFees.get(1)));
        if (!labels.isEmpty()) {
            return StringUtils.join(labels, ", ");
        } else {
            return i18n.tr("None");
        }
    }

    private Vector<String> makePaymentFeeLabelList(PaymentFeesDTO fees) {
        Vector<String> labels = new Vector<String>();
        return labels;
    }

    private String makePaymentFeeLabel(IPrimitive<BigDecimal> fee, PaymentFeesDTO.PaymentFeePolicy measure) {
        return null;
    }

    private <T> void addIfNotNull(Collection<T> collection, T value) {
        if (value != null) {
            collection.add(value);
        }
    }

}
