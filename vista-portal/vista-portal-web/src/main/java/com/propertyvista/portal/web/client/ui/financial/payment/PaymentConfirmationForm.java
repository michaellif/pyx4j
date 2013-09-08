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
package com.propertyvista.portal.web.client.ui.financial.payment;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.web.client.ui.AbstractEntityView;
import com.propertyvista.portal.web.client.ui.AbstractPortalForm;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class PaymentConfirmationForm extends AbstractPortalForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentConfirmationForm.class);

    private final AbstractEntityView<PaymentRecordDTO> view;

    public PaymentConfirmationForm(AbstractEntityView<PaymentRecordDTO> view) {
        super(PaymentRecordDTO.class);
        this.view = view;
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;
        Widget w;

        mainPanel.setWidget(++row, 0, w = new HTML(i18n.tr("Payment Submitted Successfully!")));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        w.getElement().getStyle().setFontSize(1.2, Unit.EM);

        mainPanel.setBR(++row, 0, 1);

        mainPanel.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 250).labelAlignment(Alignment.left).build());
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount()), 100).labelAlignment(Alignment.left).build());

        mainPanel.setHR(++row, 0, 1);

        mainPanel.setWidget(++row, 0, createAutoPaySignupPanel());

        return mainPanel;
    }

    @Override
    public IDecorator<AbstractPortalForm<PaymentRecordDTO>> createDecorator() {
        return new PortalContentHolder(ThemeColor.contrast4);
    }

    private Widget createAutoPaySignupPanel() {
        VerticalPanel text = new VerticalPanel();
        text.add(new HTML(i18n.tr("Want an Easy way to save time on your payments?")));
        text.add(new HTML(i18n.tr("Let us manage your monthly payments for you.")));
        text.add(new Anchor(i18n.tr("Sign up for Auto Pay today"), new Command() {
            @Override
            public void execute() {
                ((PaymentConfirmationView.Presenter) view.getPresenter()).goToAutoPay();
            }
        }));

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Image(VistaImages.INSTANCE.recurringCredit()));
        panel.add(text);

        return panel;
    }
}
