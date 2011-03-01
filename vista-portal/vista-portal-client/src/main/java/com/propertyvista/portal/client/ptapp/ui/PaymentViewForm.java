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
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.payment.CreditCardInfo;
import com.propertyvista.portal.domain.payment.EcheckInfo;
import com.propertyvista.portal.domain.payment.PaymentType;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.PaymentInfo;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CRadioGroup;

public class PaymentViewForm extends BaseEntityForm<PaymentInfo> {

    private static I18n i18n = I18nFactory.getI18n(PaymentViewForm.class);

    public PaymentViewForm() {
        super(PaymentInfo.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new ViewHeaderDecorator(proto().applicationCharges()));
        main.add(create(proto().applicationCharges().charges(), this));

        main.add(new ViewLineSeparator(0, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM));

        FlowPanel applicationFeePanel = new FlowPanel();
        applicationFeePanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        applicationFeePanel.add(DecorationUtils.inline(create(proto().applicationFee().type(), this), "60%", null));
        applicationFeePanel.add(DecorationUtils.inline(create(proto().applicationFee().charge(), this), "10%", "right"));
        main.add(applicationFeePanel);

        main.add(new HTML(SiteResources.INSTANCE.paymentApprovalNotes().getText()));

        main.add(new ViewHeaderDecorator(proto().type()));
        @SuppressWarnings("unchecked")
        CRadioGroup<PaymentType> paymentType = (CRadioGroup<PaymentType>) create(proto().type(), this);
        paymentType.addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                setInstrumentsVisibility(event.getValue());
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
        CCheckBox sameAsCurrent = (CCheckBox) create(proto().sameAsCurrent(), this);
        main.add(new VistaWidgetDecorator(sameAsCurrent, decorData));
        sameAsCurrent.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setAsCurrentAddress(event.getValue());
            }
        });

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

    private void setInstrumentsVisibility(PaymentType value) {
        boolean card = (value != PaymentType.Echeck);

        get(proto().echeck()).setVisible(!card);
        get(proto().creditCard()).setVisible(card);
    }

    private void setAsCurrentAddress(Boolean value) {
        boolean editable = true;
        if (value == Boolean.TRUE) {
            //TODO use a better forms and copy of data
            get(proto().billingAddress().street1()).setValue(getValue().currentAddress().street1().getValue());
            get(proto().billingAddress().street2()).setValue(getValue().currentAddress().street2().getValue());
            get(proto().billingAddress().city()).setValue(getValue().currentAddress().city().getValue());
            get(proto().billingAddress().postalCode()).setValue(getValue().currentAddress().postalCode().getValue());
            get(proto().billingAddress().province()).setValue(getValue().currentAddress().province().getValue());
            get(proto().billingAddress().phone()).setValue(getValue().currentPhone().getValue());
            editable = false;
        }

        get(proto().billingAddress().street1()).setEditable(editable);
        get(proto().billingAddress().street2()).setEditable(editable);
        get(proto().billingAddress().city()).setEditable(editable);
        get(proto().billingAddress().postalCode()).setEditable(editable);
        get(proto().billingAddress().province()).setEditable(editable);
        get(proto().billingAddress().phone()).setEditable(editable);
    }

    @Override
    public void populate(PaymentInfo value) {
        super.populate(value);
        setInstrumentsVisibility(value.type().getValue());
        setAsCurrentAddress(value.sameAsCurrent().getValue());
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

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.getValueClass().equals(ChargeLine.class)) {
            return new ChargeLineFolder(this);
        } else {
            return super.createMemberFolderEditor(member);
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
