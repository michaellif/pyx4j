/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.payment;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.client.ui.residents.BasicViewImpl;

public class PaymentSubmittedViewForm extends CEntityDecoratableForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentSubmittedViewForm.class);

    private final BasicViewImpl<PaymentRecordDTO> view;

    public PaymentSubmittedViewForm(BasicViewImpl<PaymentRecordDTO> view) {
        super(PaymentRecordDTO.class);
        this.view = view;
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;
        Widget w;

        panel.setWidget(++row, 0, w = new HTML(i18n.tr("Payment Submitted Successfully!")));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        w.getElement().getStyle().setFontSize(1.2, Unit.EM);

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10, 10).labelAlignment(Alignment.left).build());
        panel.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 30, 10).labelAlignment(Alignment.left).build());

        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, createLegalTermsPanel());
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    private Widget createLegalTermsPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;

        panel.setWidget(++row, 0, new HTML(i18n.tr("Want an Easy way to save time on your payments?")));
        panel.setWidget(++row, 0, new HTML(i18n.tr("Let us manage your monthly payments for you.")));
        panel.setWidget(++row, 0, new Anchor(i18n.tr("Sign up for Auto Pay today"), new Command() {
            @Override
            public void execute() {
                ((PaymentSubmittedView.Presenter) view.getPresenter()).goToAutoPay();
            }
        }));

        return panel;
    }
}
