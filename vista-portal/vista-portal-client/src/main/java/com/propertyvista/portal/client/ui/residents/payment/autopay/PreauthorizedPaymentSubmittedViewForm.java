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
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentSubmittedViewForm extends CEntityDecoratableForm<PreauthorizedPaymentDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentSubmittedViewForm.class);

    private final SimplePanel contentHolder = new SimplePanel();

    public PreauthorizedPaymentSubmittedViewForm() {
        super(PreauthorizedPaymentDTO.class);
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        return contentHolder;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        contentHolder.clear();
        contentHolder.setWidget(internalCreateContent());
    }

    public IsWidget internalCreateContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        Widget w;

        content.setWidget(++row, 0, w = new HTML(i18n.tr("Auto Payment Submitted Successfully!")));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        w.getElement().getStyle().setFontSize(1.2, Unit.EM);

        content.setBR(++row, 0, 1);

        HorizontalPanel pm = new HorizontalPanel();
        pm.add(w = new HTML(i18n.tr("Payment Method:")));
        w.setWidth("10em");
        pm.add(w = new HTML(getValue().paymentMethod().getStringView()));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        content.setWidget(++row, 0, pm);

        HorizontalPanel amount = new HorizontalPanel();
        switch (getValue().amountType().getValue()) {
        case Percent:
            amount.add(w = new HTML(i18n.tr("Percent to pay:")));
            w.setWidth("10em");
            amount.add(w = new HTML(getValue().percent().getValue().toString() + "%"));
            w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            break;

        case Value:
            amount.add(w = new HTML(i18n.tr("Amount to pay:")));
            w.setWidth("10em");
            amount.add(w = new HTML("$" + getValue().value().getValue().toString()));
            w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            break;

        default:
            break;
        }

        content.setWidget(++row, 0, amount);

        return content;
    }
}
