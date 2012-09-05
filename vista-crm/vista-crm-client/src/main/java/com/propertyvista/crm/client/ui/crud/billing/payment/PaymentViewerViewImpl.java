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
package com.propertyvista.crm.client.ui.crud.billing.payment;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentViewerViewImpl extends CrmViewerViewImplBase<PaymentRecordDTO> implements PaymentViewerView {

    private static final I18n i18n = I18n.get(PaymentViewerViewImpl.class);

    private final MenuItem scheduleAction;

    private final MenuItem processAction;

    private final MenuItem clearAction;

    private final MenuItem rejectAction;

    private final MenuItem cancelAction;

    public PaymentViewerViewImpl() {
        super(CrmSiteMap.Finance.Payment.class, new PaymentForm(true));

        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).cancelPayment();
            }
        });
        addAction(cancelAction);

        rejectAction = new MenuItem(i18n.tr("Reject"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).rejectPayment();
            }
        });
        addAction(rejectAction);

        clearAction = new MenuItem(i18n.tr("Clear"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).clearPayment();
            }
        });
        addAction(clearAction);

        processAction = new MenuItem(i18n.tr("Process"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).processPayment();
            }
        });
        processAction.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
        addAction(processAction);

        scheduleAction = new MenuItem(i18n.tr("Schedule"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).schedulePayment();
            }
        });
        scheduleAction.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
        addAction(scheduleAction);
    }

    @Override
    public void reset() {
        setActionVisible(scheduleAction, false);
        setActionVisible(processAction, false);
        setActionVisible(clearAction, false);
        setActionVisible(rejectAction, false);
        setActionVisible(cancelAction, false);
        super.reset();
    }

    @Override
    public void populate(PaymentRecordDTO value) {
        super.populate(value);

        // enable editing for submitted payments only:
        getEditButton().setVisible(value.paymentStatus().getValue() == PaymentStatus.Submitted);
        setActionVisible(cancelAction, value.paymentStatus().getValue() == PaymentStatus.Submitted);

        if (value.paymentStatus().getValue() == PaymentStatus.Submitted) {
            if (!value.targetDate().isNull() && PaymentType.schedulable().contains(value.paymentMethod().type().getValue())) {
                setActionVisible(scheduleAction, true);
            } else {
                setActionVisible(processAction, true);
            }
        }

        switch (value.paymentMethod().type().getValue()) {
        case Check:
            setActionVisible(clearAction, value.paymentStatus().getValue() == PaymentStatus.Received);
            setActionVisible(rejectAction, value.paymentStatus().getValue() == PaymentStatus.Received);
            break;
        default:
            // No other special handling for payment types
        }

    }
}