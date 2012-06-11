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
                : new LogicalDate();
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
                                .param(Params.PAYMENT_STATUS_FILTER.name(), LabelHelper.makeListView(paymentStatusCriteria))
                                .param(Params.PAYMENT_FEES.name(), i18n.tr("Note: the following payment fees are applied: {0}", makePaymentFeesLabel(paymentFees)))
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
        StringBuilder label = new StringBuilder();
        PaymentFeesDTO absFees;
        PaymentFeesDTO relFees;
        if (paymentFees[0].paymentFeeMeasure().getValue() == PaymentFeeMeasure.absolute) {
            absFees = paymentFees[0];
            relFees = paymentFees[1];
        } else {
            absFees = paymentFees[1];
            relFees = paymentFees[0];
        }

        boolean hasAbs = false;
        Vector<String> absLabels = makePaymentFeeLabelList(absFees);
        if (!absLabels.isEmpty()) {
            hasAbs = true;
            label.append(i18n.tr("$ per transaction: {0}", StringUtils.join(absLabels, ", ")));
        }

        Vector<String> relLabels = makePaymentFeeLabelList(relFees);
        if (!relLabels.isEmpty()) {
            if (hasAbs) {
                label.append("; ");
            }
            label.append(i18n.tr("% per transaction: {0}", StringUtils.join(relLabels, ", ")));
        }

        return label.toString();
    }

    private Vector<String> makePaymentFeeLabelList(PaymentFeesDTO fees) {
        Vector<String> labels = new Vector<String>();
        addIfNotNull(labels, makePaymentFeeLabel(fees.cash()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.cheque()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.eCheque()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.eft()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.cc()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.interacCaledon()));
        addIfNotNull(labels, makePaymentFeeLabel(fees.interacVisa()));
        return labels;
    }

    private String makePaymentFeeLabel(IPrimitive<BigDecimal> fee) {
        final BigDecimal zero = new BigDecimal("0.00");
        if (!fee.getValue().equals(zero)) {
            return fee.getMeta().getCaption() + " " + fee.getValue().toString();
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
