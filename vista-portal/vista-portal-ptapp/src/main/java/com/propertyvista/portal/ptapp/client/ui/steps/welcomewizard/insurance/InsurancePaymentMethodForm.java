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
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsurancePaymentMethodDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsurancePaymentMethodDTO.PaymentMethod;

public class InsurancePaymentMethodForm extends CEntityDecoratableForm<InsurancePaymentMethodDTO> {

    private static final I18n i18n = I18n.get(InsurancePaymentMethodForm.class);

    private FlowPanel paymentTypeImagesPanel;

    private CEntityForm<AddressSimple> billingAddress;

    private final boolean twoColumns;

    private AddressSimpleEditor currentAddress;

    public InsurancePaymentMethodForm() {
        this(false);
    }

    public InsurancePaymentMethodForm(boolean twoColumns) {
        super(InsurancePaymentMethodDTO.class);
        this.twoColumns = twoColumns;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel container = new FormFlexPanel();

        container.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditor.name());

        int row = -1;

        container.setH1(++row, 0, 3, proto().paymentMethod().getMeta().getCaption());

        CRadioGroupEnum<InsurancePaymentMethodDTO.PaymentMethod> radioGroup = new CRadioGroupEnum<InsurancePaymentMethodDTO.PaymentMethod>(
                InsurancePaymentMethodDTO.PaymentMethod.class, RadioGroup.Layout.VERTICAL);

        paymentTypeImagesPanel = new FlowPanel();
        paymentTypeImagesPanel.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorImages.name());
        Image paymentTypeImage;
        FlowPanel holder;

        for (PaymentMethod type : InsurancePaymentMethodDTO.PaymentMethod.values()) {
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

        CRadioGroup<PaymentType> paymentType = (CRadioGroup<PaymentType>) inject(proto().paymentMethod(), radioGroup);

        paymentType.asWidget().setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorButtons.name());

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().addClassName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorForm.name());

        instrumentsPanel.add(inject(proto().creditCard(), createCreditCardInfoEditor()));

        container.setWidget(row, 1, paymentType);

        container.setWidget(row, 2, instrumentsPanel);

        container.setH1(++row, 0, 3, proto().billingAddress().getMeta().getCaption());

        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());

        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        get(proto().sameAsCurrent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onBillingAddressSameAsCurrentOne(event.getValue());
                billingAddress.setEditable(!event.getValue());
                billingAddress.setVisible(!event.getValue());

                currentAddress.setVisible(event.getValue());
            }
        });

        container.setWidget(++row, 0, inject(proto().billingAddress(), billingAddress = new AddressSimpleEditor()));
        container.getFlexCellFormatter().setColSpan(row, 0, 3);
        container.setWidget(++row, 0, inject(proto().currentAddress(), currentAddress = new AddressSimpleEditor()));
        container.getFlexCellFormatter().setColSpan(row, 0, 3);
        currentAddress.inheritViewable(false);
        currentAddress.setViewable(true);
        currentAddress.setVisible(false);

        container.setHR(++row, 0, 3);
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().phone()), 15).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setWidth("100%");

        paymentType.setValue(PaymentType.Echeck);

        return container;

    }

    public void onBillingAddressSameAsCurrentOne(boolean set) {
        // Implements meaningful in derived classes...
    }

    private CEntityForm<CreditCardInfo> createCreditCardInfoEditor() {
        return new CEntityForm<CreditCardInfo>(CreditCardInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();
//                panel.add(new InnerPanelWidgetDecorator(inject(proto().number())));                
//                panel.add(new InnerPanelWidgetDecorator(inject(proto().expiryDate())));
//                panel.add(new InnerPanelWidgetDecorator(inject(proto().nameOn())));
//                panel.add(new InnerPanelWidgetDecorator(inject(proto().securityCode())));
////                panel.add(new InnerPanelWidgetDecorator(inject(proto().bankPhone())));
                addNotMandatory(panel, proto().number());
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
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = super.create(member);
                if (comp instanceof CMonthYearPicker) {
                    Date now = new Date();
                    @SuppressWarnings("deprecation")
                    int y = 1900 + now.getYear();
                    ((CMonthYearPicker) comp).setYearRange(new Range(y, 10));

//                    ((CMonthYearPicker) comp).addValueValidator(new EditableValueValidator<Date>() {
//
//                        @Override
//                        public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
//                            if (value == null) {
//                                return null;
//                            } else {
//                                Date now = new Date();
//                                @SuppressWarnings("deprecation")
//                                Date thisMonth = new Date(now.getYear(), now.getMonth(), 1);
//                                return value.compareTo(thisMonth) >= 0 ? null : new ValidationFailure(i18n.tr("Card expiry should be a future date"));
//                            }
//                        }
//
//                    });
                }
                return comp;
            }

            @Override
            public void addValidations() {
                super.addValidations();
//                get(proto().number()).addValueValidator(new CreditCardNumberValidator());
            }
        };
    }

    class InnerPanelWidgetDecorator extends WidgetDecorator {

        public InnerPanelWidgetDecorator(CComponent<?, ?> component) {
            super(new Builder(component).labelWidth(12).componentWidth(12));
        }

    }
}
