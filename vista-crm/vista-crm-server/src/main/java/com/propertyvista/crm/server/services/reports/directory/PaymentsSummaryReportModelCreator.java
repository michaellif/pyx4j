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

import static com.propertyvista.crm.server.services.reports.Util.asStubs;
import static com.propertyvista.crm.server.services.reports.Util.getSortingCriteria;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.crm.server.services.dashboard.gadgets.PaymentReportServiceImpl;
import com.propertyvista.crm.server.services.reports.DynamicTableTemplateReportModelBuilder;
import com.propertyvista.crm.server.services.reports.GadgetReportModelCreator;
import com.propertyvista.crm.server.services.reports.ReportsCommon;
import com.propertyvista.crm.server.services.reports.util.DynamicColumnWidthReportTableTemplateBuilder;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO.PaymentFeeMeasure;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.svg.gadgets.util.LabelHelper;

public class PaymentsSummaryReportModelCreator implements GadgetReportModelCreator {

    enum Params {

        TITLE, PAYMENT_STATUS_FILTER, PAYMENT_FEES, AS_OF

    }

    private static final I18n i18n = I18n.get(PaymentsSummaryReportModelCreator.class);

    @Override
    public void createReportModel(final AsyncCallback<JasperReportModel> callback, GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) {
        final PaymentsSummaryGadgetMetadata paymentsSummaryGadgetMetadata = gadgetMetadata.duplicate(PaymentsSummaryGadgetMetadata.class);
        final LogicalDate targetDate = paymentsSummaryGadgetMetadata.customizeDate().isBooleanTrue() ? paymentsSummaryGadgetMetadata.asOf().getValue()
                : new LogicalDate(SysDateManager.getSysDate());
        final Vector<PaymentRecord.PaymentStatus> paymentStatusCriteria = new Vector<PaymentRecord.PaymentStatus>(paymentsSummaryGadgetMetadata.paymentStatus()
                .getValue());
        final PaymentFeesDTO[] paymentFees = new PaymentFeesDTO[2];

        new PaymentReportServiceImpl().paymentsFees(//@formatter:off
                new AsyncCallback<Vector<PaymentFeesDTO>>() {
                    
                    @Override
                    public void onSuccess(Vector<PaymentFeesDTO> result) {
                        paymentFees[0] = result.get(0);
                        paymentFees[1] = result.get(1);
                    }
                    
                    @Override
                    public void onFailure(Throwable error) {
                        throw new RuntimeException(error);
                    }

                }
        );//@formatter:on

        new PaymentReportServiceImpl().paymentsSummary(//@formatter:off                
                new AsyncCallback<EntitySearchResult<PaymentsSummary>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<PaymentsSummary> result) {
                        String template = new DynamicColumnWidthReportTableTemplateBuilder(EntityFactory.getEntityPrototype(PaymentsSummary.class), paymentsSummaryGadgetMetadata)                                
                                .defSubTitle(Params.AS_OF.name())
                                .defSubTitle(Params.PAYMENT_STATUS_FILTER.name())
                                .defSubTitle(Params.PAYMENT_FEES.name())
                                .build();
                        
                        JasperReportModel model = new DynamicTableTemplateReportModelBuilder()
                                .template(template)
                                .data(result.getData().iterator())
                                .param(Params.TITLE.name(), paymentsSummaryGadgetMetadata.getEntityMeta().getCaption())
                                .param(Params.AS_OF.name(), i18n.tr("As of Date: {0}", ReportsCommon.instance().getAsOfDateFormat().format(targetDate)))
                                .param(Params.PAYMENT_STATUS_FILTER.name(), i18n.tr("Payment Status Filter: {0}", LabelHelper.makeListView(paymentStatusCriteria)))
                                .param(Params.PAYMENT_FEES.name(), i18n.tr("The following payment fees are applied (per transaction): {0}", makePaymentFeesLabel(paymentFees)))
                                .build();
                        callback.onSuccess(model);
                    }
                    
                    @Override
                    public void onFailure(Throwable error) {
                        callback.onFailure(error);
                    }
                },
                asStubs(selectedBuildings),
                targetDate,
                paymentStatusCriteria,
                0,
                0,
                getSortingCriteria(paymentsSummaryGadgetMetadata)
        );//@formatter:on

    }

    private String makePaymentFeesLabel(PaymentFeesDTO[] paymentFees) {

        Vector<String> labels = new Vector<String>();
        labels.addAll(makePaymentFeeLabelList(paymentFees[0]));
        labels.addAll(makePaymentFeeLabelList(paymentFees[1]));
        if (!labels.isEmpty()) {
            return StringUtils.join(labels, ", ");
        } else {
            return i18n.tr("None");
        }
    }

    private Vector<String> makePaymentFeeLabelList(PaymentFeesDTO fees) {
        Vector<String> labels = new Vector<String>();
        addIfNotNull(labels, makePaymentFeeLabel(fees.cash(), fees.paymentFeeMeasure().getValue()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.cheque(), fees.paymentFeeMeasure().getValue()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.eCheque(), fees.paymentFeeMeasure().getValue()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.eft(), fees.paymentFeeMeasure().getValue()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.cc(), fees.paymentFeeMeasure().getValue()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.interacCaledon(), fees.paymentFeeMeasure().getValue()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.interacVisa(), fees.paymentFeeMeasure().getValue()));
        return labels;
    }

    private String makePaymentFeeLabel(IPrimitive<BigDecimal> fee, PaymentFeesDTO.PaymentFeeMeasure measure) {
        final BigDecimal zero = new BigDecimal("0.00");
        if (!fee.isNull() & !zero.equals(fee.getValue())) {
            StringBuilder label = new StringBuilder();

            label.append(fee.getMeta().getCaption());
            label.append(' ');

            if (measure == PaymentFeeMeasure.absolute) {
                label.append("$");
            }
            label.append(fee.getValue().toString());
            if (measure == PaymentFeeMeasure.relative) {
                label.append("%");
            }
            return label.toString();
        } else {
            return null;
        }
    }

    private <T> void addIfNotNull(Collection<T> collection, T value) {
        if (value != null) {
            collection.add(value);
        }
    }

}
