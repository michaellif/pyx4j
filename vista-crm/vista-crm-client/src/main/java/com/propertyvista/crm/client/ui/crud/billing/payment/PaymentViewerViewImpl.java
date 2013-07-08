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

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.gwt.commons.Print;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentViewerViewImpl extends CrmViewerViewImplBase<PaymentRecordDTO> implements PaymentViewerView {

    private static final I18n i18n = I18n.get(PaymentViewerViewImpl.class);

    private final MenuItem scheduleAction;

    private final MenuItem processAction;

    private final MenuItem clearAction;

    private final MenuItem rejectAction;

    private final MenuItem rejectNSFAction;

    private final MenuItem cancelAction;

    public PaymentViewerViewImpl() {
        setForm(new PaymentForm(this));

        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).cancelPayment();
            }
        });
        addAction(cancelAction);

        rejectNSFAction = new MenuItem(i18n.tr("Reject with NSF"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).rejectPayment(true);
            }
        });
        addAction(rejectNSFAction);

        rejectAction = new MenuItem(i18n.tr("Reject Other"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).rejectPayment(false);
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
        addAction(processAction);

        scheduleAction = new MenuItem(i18n.tr("Schedule"), new Command() {
            @Override
            public void execute() {
                ((PaymentViewerView.Presenter) getPresenter()).schedulePayment();
            }
        });
        addAction(scheduleAction);

        addHeaderToolbarItem(new Button(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                print();
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
        setEditingVisible(value.paymentStatus().getValue() == PaymentStatus.Submitted);
        setActionVisible(cancelAction, value.paymentStatus().getValue().isCancelable());

        if (value.paymentStatus().getValue() == PaymentStatus.Submitted) {
            if (!value.targetDate().isNull() && value.paymentMethod().type().getValue().isSchedulable()) {
                setActionVisible(scheduleAction, true);
            } else {
                setActionVisible(processAction, true);
                setActionHighlighted(processAction, true);
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

    private void print() {
        String html = getForm().toStringForPrint();
        html = html.replace("<body>", "").replace("</body>", "");
        String logo = "<div style=\"text-align: center;\"><img src=\"" + MediaUtils.createCrmLogoUrl() + "\"></div>";
        String header = "<div style=\"text-align: center;\"><h1>" + SafeHtmlUtils.htmlEscape(i18n.tr("Payment {0}", getForm().getValue().id().getValue()))
                + "</h1></div>";
        html = logo + header + "<body>" + html + "</body>";
        Print.preview(html);
    }
}