/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.insurance;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.moveinwizardmockup.InsurancePaymentMethodMockupDTO;
import com.propertyvista.domain.moveinwizardmockup.InsurancePaymentMethodMockupDTO.PaymentMethod;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardNumberIdentity;

public class InsurancePaymentMethodForm extends CEntityDecoratableForm<InsurancePaymentMethodMockupDTO> {

    private static final I18n i18n = I18n.get(InsurancePaymentMethodForm.class);

    private FlowPanel paymentTypeImagesPanel;

    private CEntityForm<AddressSimple> billingAddress;

    private final boolean twoColumns;

    private AddressSimpleEditor currentAddress;

    public InsurancePaymentMethodForm() {
        this(false);
        setMandatory(false);
    }

    public InsurancePaymentMethodForm(boolean twoColumns) {
        super(InsurancePaymentMethodMockupDTO.class);
        this.twoColumns = twoColumns;
    }

    @Override
    public IsWidget createContent() {

        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel();

        container.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditor.name());

        int row = -1;

        container.setH1(++row, 0, 3, proto().paymentMethod().getMeta().getCaption());

        CRadioGroupEnum<InsurancePaymentMethodMockupDTO.PaymentMethod> radioGroup = new CRadioGroupEnum<InsurancePaymentMethodMockupDTO.PaymentMethod>(
                InsurancePaymentMethodMockupDTO.PaymentMethod.class, RadioGroup.Layout.VERTICAL);

        paymentTypeImagesPanel = new FlowPanel();
        paymentTypeImagesPanel.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorImages.name());
        Image paymentTypeImage;
        FlowPanel holder;

        for (PaymentMethod type : InsurancePaymentMethodMockupDTO.PaymentMethod.values()) {
            switch (type) {
            case Visa:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentVISA().getSafeUri());
                break;
            case MasterCard:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentMC().getSafeUri());
                break;
            default:
                paymentTypeImage = null;
                break;
            }
            if (paymentTypeImage != null) {
                paymentTypeImage.setHeight("20px");
                holder = new FlowPanel();
                holder.add(paymentTypeImage);
                paymentTypeImagesPanel.add(holder);
            }
        }

        container.setWidget(++row, 0, paymentTypeImagesPanel);

        CRadioGroup<PaymentMethod> paymentMethod = inject(proto().paymentMethod(), radioGroup);

        paymentMethod.asWidget().setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorButtons.name());

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().addClassName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorForm.name());

        instrumentsPanel.add(inject(proto().creditCard(), createCreditCardInfoEditor()));

        container.setWidget(row, 1, paymentMethod);

        container.setWidget(row, 2, instrumentsPanel);

        container.setH1(++row, 0, 3, proto().billingAddress().getMeta().getCaption());

        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());

        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        get(proto().sameAsCurrent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onBillingAddressSameAsCurrentOne(event.getValue());
            }
        });

        container.setWidget(++row, 0, inject(proto().billingAddress(), billingAddress = new AddressSimpleEditor()));
        billingAddress.setMandatory(false);
        container.getFlexCellFormatter().setColSpan(row, 0, 3);
        container.setWidget(++row, 0, inject(proto().currentAddress(), currentAddress = new AddressSimpleEditor()));
        container.getFlexCellFormatter().setColSpan(row, 0, 3);
        currentAddress.inheritViewable(false);
        currentAddress.setViewable(true);
        currentAddress.setVisible(false);

        container.setHR(++row, 0, 3);
        container.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().phone()), 15).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setWidth("100%");

        paymentMethod.setValue(PaymentMethod.Visa);

        return container;

    }

    public void onBillingAddressSameAsCurrentOne(boolean isBillingAddressSameAsCurrent) {
        billingAddress.setEditable(!isBillingAddressSameAsCurrent);
        billingAddress.setVisible(!isBillingAddressSameAsCurrent);
        billingAddress.setEnabled(!isBillingAddressSameAsCurrent);
        currentAddress.setVisible(isBillingAddressSameAsCurrent);
    }

    private CEntityForm<CreditCardInfo> createCreditCardInfoEditor() {
        return new CEntityForm<CreditCardInfo>(CreditCardInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();
                panel.add(new InnerPanelWidgetDecorator(inject(proto().card(), new CPersonalIdentityField<CreditCardNumberIdentity>(
                        CreditCardNumberIdentity.class, "X XXXX XXXX xxxx;XXXX XXXX XXXX xxxx", null))));
                addNotMandatory(panel, proto().expiryDate());
                addNotMandatory(panel, proto().nameOn());
                addNotMandatory(panel, proto().securityCode());
                return panel;
            }

            private void addNotMandatory(FlowPanel panel, IObject<?> object) {
                panel.add(new InnerPanelWidgetDecorator(inject(object)));
                get(object).setMandatory(false);
            }

            @Override
            public CComponent<?> create(IObject<?> member) {
                CComponent<?> comp = super.create(member);
                if (comp instanceof CMonthYearPicker) {
                    Date now = new Date();
                    @SuppressWarnings("deprecation")
                    int y = 1900 + now.getYear();
                    ((CMonthYearPicker) comp).setYearRange(new Range(y, 10));
                }
                return comp;
            }

            @Override
            public void addValidations() {
                super.addValidations();
            }
        };
    }

    class InnerPanelWidgetDecorator extends WidgetDecorator {

        public InnerPanelWidgetDecorator(CComponent<?> component) {
            super(new Builder(component).labelWidth(12).componentWidth(12));
        }

    }
}
