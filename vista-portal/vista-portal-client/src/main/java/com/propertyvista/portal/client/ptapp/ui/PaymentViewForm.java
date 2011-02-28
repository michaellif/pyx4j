/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.pt.PaymentInfo;

public class PaymentViewForm extends BaseEntityForm<PaymentInfo> {

    private static I18n i18n = I18nFactory.getI18n(PaymentViewForm.class);

    private Widget echeckPanel;

    private Widget creditCardPanel;

    public PaymentViewForm() {
        super(PaymentInfo.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new HTML(SiteResources.INSTANCE.paymentApprovalNotes().getText()));

        main.add(new ViewHeaderDecorator(proto().type()));
        main.add(create(proto().type(), this));

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

        instrumentsPanel.add(echeckPanel = createEcheckPanel());
        instrumentsPanel.add(creditCardPanel = createCreditCardPanel());
        main.add(instrumentsPanel);

        main.add(new ViewHeaderDecorator(proto().billingAddress()));
        DecorationData decorData = new DecorationData();
        decorData.componentWidth = 12;
        main.add(new VistaWidgetDecorator(create(proto().sameAsCurrent(), this), decorData));

        createIAddress(main, proto().billingAddress(), this);

        decorData = new DecorationData();
        decorData.componentWidth = 12;
        main.add(new VistaWidgetDecorator(create(proto().billingAddress().phone(), this), decorData));

        main.add(new ViewHeaderDecorator(i18n.tr("Pre-Authorized Payment")));
        HorizontalPanel preauthorisedNotes = new HorizontalPanel();
        preauthorisedNotes.add(new HTML(SiteResources.INSTANCE.paymentPreauthorisedNotes().getText()));
        main.add(preauthorisedNotes);

        decorData = new DecorationData();
        decorData.componentWidth = 12;
        main.add(new VistaWidgetDecorator(create(proto().preauthorised(), this), decorData));

        main.add(new HTML(SiteResources.INSTANCE.paymentTermsNotes().getText()));

        setWidget(main);
    }

    private Widget createEcheckPanel() {
        FlowPanel panel = new FlowPanel();

        DecorationData decorData = new DecorationData();
        decorData.componentWidth = 12;
        panel.add(new VistaWidgetDecorator(create(proto().echeck().nameOnAccount(), this), decorData));
        panel.add(new VistaWidgetDecorator(create(proto().echeck().accountType(), this), decorData));
        panel.add(new VistaWidgetDecorator(create(proto().echeck().bankName(), this), decorData));

        HorizontalPanel numbers = new HorizontalPanel();
        numbers.add(create(proto().echeck().routingNo(), this));
        numbers.add(create(proto().echeck().accountNo(), this));
        numbers.add(create(proto().echeck().checkNo(), this));

        panel.add(numbers);

        return panel;
    }

    private Widget createCreditCardPanel() {
        FlowPanel panel = new FlowPanel();

        DecorationData decorData = new DecorationData();
        decorData.componentWidth = 12;
        panel.add(new VistaWidgetDecorator(create(proto().creditCard().cardNumber(), this), decorData));
        panel.add(new VistaWidgetDecorator(create(proto().creditCard().expiry(), this), decorData));
        panel.add(new VistaWidgetDecorator(create(proto().creditCard().exactName(), this), decorData));
        panel.add(new VistaWidgetDecorator(create(proto().creditCard().bankPhone(), this), decorData));

        return panel;
    }

}
