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
 */
package com.propertyvista.crm.client.ui.crud.billing.payment;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentRecordViewerViewImpl extends CrmViewerViewImplBase<PaymentRecordDTO> implements PaymentRecordViewerView {

    private static final I18n i18n = I18n.get(PaymentRecordViewerViewImpl.class);

    private final MenuItem scheduleAction;

    private final MenuItem processAction;

    private final MenuItem clearAction;

    private final MenuItem rejectAction;

    private final MenuItem rejectNSFAction;

    private final MenuItem cancelAction;

    public PaymentRecordViewerViewImpl() {
        setForm(new PaymentRecordForm(this));

        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                ((PaymentRecordViewerView.Presenter) getPresenter()).cancelPayment();
            }
        });
        addAction(cancelAction);

        rejectNSFAction = new MenuItem(i18n.tr("Reject with NSF"), new Command() {
            @Override
            public void execute() {
                ((PaymentRecordViewerView.Presenter) getPresenter()).rejectPayment(true);
            }
        });
        addAction(rejectNSFAction);

        rejectAction = new MenuItem(i18n.tr("Reject Other"), new Command() {
            @Override
            public void execute() {
                ((PaymentRecordViewerView.Presenter) getPresenter()).rejectPayment(false);
            }
        });
        addAction(rejectAction);

        clearAction = new MenuItem(i18n.tr("Clear"), new Command() {
            @Override
            public void execute() {
                ((PaymentRecordViewerView.Presenter) getPresenter()).clearPayment();
            }
        });
        addAction(clearAction);

        processAction = new MenuItem(i18n.tr("Process"), new Command() {
            @Override
            public void execute() {
                ((PaymentRecordViewerView.Presenter) getPresenter()).processPayment();
            }
        });
        addAction(processAction);

        scheduleAction = new MenuItem(i18n.tr("Schedule"), new Command() {
            @Override
            public void execute() {
                ((PaymentRecordViewerView.Presenter) getPresenter()).schedulePayment();
            }
        });
        addAction(scheduleAction);

        addHeaderToolbarItem(new Button(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(getForm().getPrintableElement());
            }
        }));
    }

    @Override
    public void reset() {
        setActionVisible(scheduleAction, false);
        setActionVisible(processAction, false);
        setActionVisible(clearAction, false);
        setActionVisible(rejectAction, false);
        setActionVisible(rejectNSFAction, false);
        setActionVisible(cancelAction, false);

        super.reset();
    }

    @Override
    public void populate(PaymentRecordDTO value) {
        super.populate(value);

        // enable editing for submitted payments only:
        setEditingVisible(value.paymentStatus().getValue() == PaymentStatus.Submitted && value.paymentMethod().type().getValue() != PaymentType.DirectBanking);
        setActionVisible(cancelAction, value.paymentStatus().getValue().isCancelable());

        if (value.paymentStatus().getValue() == PaymentStatus.Submitted) {
            if (!value.targetDate().isNull() && value.paymentMethod().type().getValue().isSchedulable()) {
                setActionVisible(scheduleAction, true);
                setActionHighlighted(scheduleAction, true);
            } else {
                if (!Lease.Status.noPayment().contains(value.leaseStatus().getValue())) {
                    setActionVisible(processAction, true);
                    setActionHighlighted(processAction, true);
                }
            }
        }

        switch (value.paymentMethod().type().getValue()) {
        case Check:
            setActionVisible(clearAction, value.paymentStatus().getValue().isCheckClearable());
            setActionVisible(rejectAction, value.paymentStatus().getValue().isCheckRejectable());
            setActionVisible(rejectNSFAction, value.paymentStatus().getValue().isCheckRejectable());
            break;
        default:
            // No other special handling for payment types
        }

    }

}