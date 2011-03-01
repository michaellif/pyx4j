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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.resources.SiteResources;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.EcheckInfo;
import com.propertyvista.portal.domain.payment.PaymentType;
import com.propertyvista.portal.domain.pt.PaymentInfo;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;

public class PaymentViewForm extends BaseEntityForm<PaymentInfo> {

    private static I18n i18n = I18nFactory.getI18n(PaymentViewForm.class);

    public PaymentViewForm() {
        super(PaymentInfo.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new HTML(SiteResources.INSTANCE.paymentApprovalNotes().getText()));

        main.add(new ViewHeaderDecorator(proto().type()));
        @SuppressWarnings("unchecked")
        CComboBox<PaymentType> paymentType = (CComboBox<PaymentType>) create(proto().type(), this);
        paymentType.addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setVisibility(event.getValue());
            }
        });
        main.add(paymentType);

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
        instrumentsPanel.add(create(proto().echeck(), this));
        instrumentsPanel.add(create(proto().creditCard(), this));
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

        return main;
    }

    private void setVisibility(PaymentType value) {
        boolean card = (value != PaymentType.Echeck);

        get(proto().echeck()).setVisible(!card);
        get(proto().creditCard()).setVisible(card);
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        if (member.getValueClass().equals(EcheckInfo.class)) {
            return createEcheckInfoEditor();
        } else if (member.getValueClass().equals(CreditCardInfo.class)) {
            return createCreditCardInfoEditor();
        } else {
            return super.createMemberEditor(member);
        }
    }

    private CEntityEditableComponent<EcheckInfo> createEcheckInfoEditor() {
        return new CEntityEditableComponent<EcheckInfo>(EcheckInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();

                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 12;
                panel.add(new VistaWidgetDecorator(create(proto().nameOnAccount(), this), decorData));
                panel.add(new VistaWidgetDecorator(create(proto().accountType(), this), decorData));
                panel.add(new VistaWidgetDecorator(create(proto().bankName(), this), decorData));

                HorizontalPanel numbers = new HorizontalPanel();
                numbers.add(create(proto().routingNo(), this));
                numbers.add(create(proto().accountNo(), this));
                numbers.add(create(proto().checkNo(), this));

                panel.add(numbers);
                return panel;
            }
        };
    }

    private CEntityEditableComponent<CreditCardInfo> createCreditCardInfoEditor() {
        return new CEntityEditableComponent<CreditCardInfo>(CreditCardInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();
                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 12;
                panel.add(new VistaWidgetDecorator(create(proto().cardNumber(), this), decorData));
                panel.add(new VistaWidgetDecorator(create(proto().expiry(), this), decorData));
                panel.add(new VistaWidgetDecorator(create(proto().exactName(), this), decorData));
                panel.add(new VistaWidgetDecorator(create(proto().bankPhone(), this), decorData));
                return panel;
            }
        };
    }

}
