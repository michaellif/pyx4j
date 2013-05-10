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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentSubmittedViewForm extends CEntityDecoratableForm<PreauthorizedPaymentDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentSubmittedViewForm.class);

    private final SimplePanel amountPlaceholder = new SimplePanel();

    private final Widget percent;

    private final Widget value;

    public PreauthorizedPaymentSubmittedViewForm() {
        super(PreauthorizedPaymentDTO.class);
        setViewable(true);
        inheritViewable(false);

        amountPlaceholder.setWidth("15em");
        percent = new DecoratorBuilder(inject(proto().percent()), 10, 10).labelAlignment(Alignment.left).build();
        value = new DecoratorBuilder(inject(proto().value()), 10, 10).labelAlignment(Alignment.left).build();
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        Widget w;

        content.setWidget(++row, 0, w = new HTML(i18n.tr("Automatic Payment Submitted Successfully!")));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        w.getElement().getStyle().setFontSize(1.2, Unit.EM);

        content.setBR(++row, 0, 1);

        content.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 30, 10).labelAlignment(Alignment.left).build());
        content.setWidget(++row, 0, amountPlaceholder);

        content.setHR(++row, 0, 1);

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel()), 10).labelAlignment(Alignment.left)
                .labelWidth(25).build());

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setAmountEditor(getValue().amountType().getValue());
    }

    private void setAmountEditor(AmountType amountType) {
        amountPlaceholder.clear();
        get(proto().percent()).setVisible(false);
        get(proto().value()).setVisible(false);

        if (amountType != null) {
            switch (amountType) {
            case Percent:
                amountPlaceholder.setWidget(percent);
                get(proto().percent()).setVisible(true);
                break;

            case Value:
                amountPlaceholder.setWidget(value);
                get(proto().value()).setVisible(true);
                break;

            default:
                throw new IllegalArgumentException();
            }
        }
    }
}
