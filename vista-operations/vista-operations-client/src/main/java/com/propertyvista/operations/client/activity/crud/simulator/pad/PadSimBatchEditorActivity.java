/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulator.pad;

import java.math.BigDecimal;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.pad.batch.PadSimBatchEditorView;
import com.propertyvista.operations.domain.payment.pad.MerchantReconciliationStatus;
import com.propertyvista.operations.domain.payment.pad.TransactionReconciliationStatus;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimDebitRecord;
import com.propertyvista.operations.rpc.services.simulator.PadSimBatchCrudService;

public class PadSimBatchEditorActivity extends AbstractEditorActivity<PadSimBatch> implements PadSimBatchEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public PadSimBatchEditorActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(PadSimBatchEditorView.class), (AbstractCrudService<PadSimBatch>) GWT
                .create(PadSimBatchCrudService.class), PadSimBatch.class);
    }

    @Override
    public void calculate() {
        PadSimBatch batch = getView().getValue();
        updateReconciliation(batch);
        onPopulateSuccess(batch);
    }

    private class SummaryTotal {

        int recordsCount = 0;

        BigDecimal totalAmount = new BigDecimal("0");

        void add(String amountValue) {
            recordsCount++;
            totalAmount = totalAmount.add(PadSimUtils.parsAmount(amountValue));
        }
    }

    private void updateReconciliation(PadSimBatch padBatch) {
        if (padBatch.reconciliationStatus().isNull()) {
            padBatch.reconciliationStatus().setValue(MerchantReconciliationStatus.PAID);
        }
        SummaryTotal total = new SummaryTotal();
        SummaryTotal gross = new SummaryTotal();
        SummaryTotal rejects = new SummaryTotal();
        SummaryTotal returns = new SummaryTotal();

        for (PadSimDebitRecord record : padBatch.records()) {
            if (record.acknowledgmentStatusCode().isNull()) {
                if (record.paymentDate().isNull()) {
                    record.paymentDate().setValue(PadSimUtils.formatDate(ClientContext.getServerDate()));
                }
                if (record.reconciliationStatus().isNull()) {
                    record.reconciliationStatus().setValue(TransactionReconciliationStatus.PROCESSED);
                }
                switch (record.reconciliationStatus().getValue()) {
                case PROCESSED:
                    gross.add(record.amount().getValue());
                    break;
                case REJECTED:
                    rejects.add(record.amount().getValue());
                    break;
                case RETURNED:
                    returns.add(record.amount().getValue());
                    break;
                default:
                    break;
                }
                total.add(record.amount().getValue());
            }
        }
        if (padBatch.recordsCount().isNull()) {
            padBatch.recordsCount().setValue(padBatch.records().size());
        }
        if (padBatch.batchAmount().isNull()) {
            padBatch.batchAmount().setValue(PadSimUtils.formatAmount(total.totalAmount));
        }

        if (padBatch.grossPaymentCount().isNull()) {
            padBatch.grossPaymentCount().setValue(String.valueOf(gross.recordsCount));
        }
        if (padBatch.grossPaymentAmount().isNull()) {
            padBatch.grossPaymentAmount().setValue(PadSimUtils.formatAmount(gross.totalAmount));
        }

        if (padBatch.rejectItemsCount().isNull()) {
            padBatch.rejectItemsCount().setValue(String.valueOf(rejects.recordsCount));
        }
        if (padBatch.rejectItemsAmount().isNull()) {
            padBatch.rejectItemsAmount().setValue(PadSimUtils.formatAmount(rejects.totalAmount));
        }

        if (padBatch.returnItemsCount().isNull()) {
            padBatch.returnItemsCount().setValue(String.valueOf(returns.recordsCount));
        }
        if (padBatch.returnItemsAmount().isNull()) {
            padBatch.returnItemsAmount().setValue(PadSimUtils.formatAmount(returns.totalAmount));
        }

    }
}
