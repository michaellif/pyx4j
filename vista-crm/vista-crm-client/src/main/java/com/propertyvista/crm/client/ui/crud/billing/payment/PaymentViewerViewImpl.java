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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentViewerViewImpl extends CrmViewerViewImplBase<PaymentRecordDTO> implements PaymentViewerView {

    private static final I18n i18n = I18n.get(PaymentViewerViewImpl.class);

    private final Button processAction;

    private final Button clearAction;

    private final Button rejectAction;

    private final Button cancelAction;

    public PaymentViewerViewImpl() {
        super(CrmSiteMap.Tenants.Payment.class, new PaymentForm(true));

        cancelAction = new Button(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PaymentViewerView.Presenter) presenter).cancelPayment();
            }
        });
        addHeaderToolbarTwoItem(cancelAction.asWidget());

        rejectAction = new Button(i18n.tr("Reject"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PaymentViewerView.Presenter) presenter).rejectPayment();
            }
        });
        addHeaderToolbarTwoItem(rejectAction.asWidget());

        clearAction = new Button(i18n.tr("Clear"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PaymentViewerView.Presenter) presenter).clearPayment();
            }
        });
        addHeaderToolbarTwoItem(clearAction.asWidget());

        processAction = new Button(i18n.tr("Process"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PaymentViewerView.Presenter) presenter).processPayment();
            }
        });
        processAction.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
        addHeaderToolbarTwoItem(processAction.asWidget());

    }

    @Override
    public void reset() {
        processAction.setVisible(false);
        clearAction.setVisible(false);
        rejectAction.setVisible(false);
        cancelAction.setVisible(false);
        super.reset();
    }

    @Override
    public void populate(PaymentRecordDTO value) {
        super.populate(value);

        processAction.setVisible(value.paymentStatus().getValue() == PaymentStatus.Submitted);
        clearAction.setVisible(value.paymentStatus().getValue() == PaymentStatus.Processing);
        rejectAction.setVisible(value.paymentStatus().getValue() == PaymentStatus.Processing);
        cancelAction.setVisible(value.paymentStatus().getValue() == PaymentStatus.Submitted);

        // enable editing for submitted payments only:
        getEditButton().setVisible(value.paymentStatus().getValue() == PaymentStatus.Submitted);
    }

}