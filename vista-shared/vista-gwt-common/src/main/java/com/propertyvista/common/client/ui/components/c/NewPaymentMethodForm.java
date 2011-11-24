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
package com.propertyvista.common.client.ui.components.c;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.validators.CreditCardNumberValidator;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class NewPaymentMethodForm extends CEntityDecoratableEditor<PaymentMethod> {

    private FlowPanel paymentTypeImagesPanel;

    private CEntityEditor<AddressStructured> billingAddress;

    private final boolean twoColumns;

    public NewPaymentMethodForm() {
        this(false);
    }

    public NewPaymentMethodForm(boolean twoColumns) {
        super(PaymentMethod.class, new VistaEditorsComponentFactory());
        this.twoColumns = twoColumns;
    }

    @Override
    public void populate(PaymentMethod value) {
        super.populate(value);
        setInstrumentsVisibility(value.type().getValue());
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel container = new FormFlexPanel();

        container.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditor.name());

        int row = -1;

        container.setH1(++row, 0, 3, proto().type().getMeta().getCaption());

        CRadioGroupEnum<PaymentType> radioGroup = new CRadioGroupEnum<PaymentType>(PaymentType.class, CRadioGroup.Layout.VERTICAL);

        paymentTypeImagesPanel = new FlowPanel();
        paymentTypeImagesPanel.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorImages.name());
        Image paymentTypeImage;
        FlowPanel holder;
        for (int i = 0; i < PaymentType.values().length; i++) {
            switch (i) {
            case 0:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentACH().getSafeUri());
                break;
            case 1:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentVISA().getSafeUri());
                break;
            case 2:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentAMEX().getSafeUri());
                break;
            case 3:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentMC().getSafeUri());
                break;
            case 4:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentDiscover().getSafeUri());
                break;
            default:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentInterac().getSafeUri());
                break;
            }
            paymentTypeImage.setHeight("20px");
            holder = new FlowPanel();
            holder.add(paymentTypeImage);
            paymentTypeImagesPanel.add(holder);
        }

        container.setWidget(++row, 0, paymentTypeImagesPanel);

        CRadioGroup<PaymentType> paymentType = (CRadioGroup<PaymentType>) inject(proto().type(), radioGroup);

        paymentType.asWidget().setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorButtons.name());

        paymentType.addValueChangeHandler(new ValueChangeHandler<PaymentType>() {

            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                int index = event.getValue().ordinal();
                setPaymentTableVisibility(index);
                setInstrumentsVisibility(event.getValue());
            }
        });

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().addClassName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorForm.name());

        instrumentsPanel.add(inject(proto().echeck(), createEcheckInfoEditor()));
        instrumentsPanel.add(inject(proto().creditCard(), createCreditCardInfoEditor()));

        container.setWidget(row, 1, paymentType);

        container.setWidget(row, 2, instrumentsPanel);

        setPaymentTableVisibility(0);

        container.setH1(++row, 0, 3, proto().billingAddress().getMeta().getCaption());

        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        CComponent<?, ?> comp = get(proto().sameAsCurrent());
        if (comp instanceof CCheckBox) {
            ((CCheckBox) comp).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    onBillingAddressSameAsCurrentOne(event.getValue());
                    billingAddress.setEditable(!event.getValue());
                }
            });
        }

        container.setWidget(++row, 0, inject(proto().billingAddress(), billingAddress = new AddressStructuredEditor(twoColumns)));
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setHR(++row, 0, 3);
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().phone()), 15).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setWidth("100%");
        setInstrumentsVisibility(PaymentType.Echeck);
        return container;

    }

    public void onBillingAddressSameAsCurrentOne(boolean set) {
        // Implements meaningful in derived classes...  
    }

    private void setInstrumentsVisibility(PaymentType value) {
        boolean card = (value != PaymentType.Echeck);
        get(proto().echeck()).setVisible(!card);
        get(proto().creditCard()).setVisible(card);
    }

    private void setPaymentTableVisibility(int index) {
        int count = paymentTypeImagesPanel.getWidgetCount();
        for (int i = 0; i < count; i++) {
            paymentTypeImagesPanel.getWidget(i).removeStyleName(NewPaymentMethodEditorTheme.StyleDependent.selected.name());
        }
        paymentTypeImagesPanel.getWidget(index).addStyleName(NewPaymentMethodEditorTheme.StyleDependent.selected.name());
    }

    private CEntityEditor<EcheckInfo> createEcheckInfoEditor() {
        return new CEntityEditor<EcheckInfo>(EcheckInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();

                panel.add(new InnerPanelWidgetDecorator(inject(proto().nameOnAccount())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().accountType())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().bankName())));

                CheckPanel checkPanel = new CheckPanel(this);
                checkPanel.add(proto().routingNo(), 85);
                checkPanel.add(proto().accountNo(), 85);
                checkPanel.add(proto().checkNo(), 58);

                panel.add(checkPanel);

                return panel;
            }
        };

    }

    private CEntityEditor<CreditCardInfo> createCreditCardInfoEditor() {
        return new CEntityEditor<CreditCardInfo>(CreditCardInfo.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel panel = new FlowPanel();
                panel.add(new InnerPanelWidgetDecorator(inject(proto().cardNumber())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().expiry())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().exactName())));
                panel.add(new InnerPanelWidgetDecorator(inject(proto().bankPhone())));
                return panel;
            }

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                CComponent<?, ?> comp = super.create(member);
                if (comp instanceof CMonthYearPicker) {
                    Date now = new Date();
                    @SuppressWarnings("deprecation")
                    int y = 1900 + now.getYear();
                    ((CMonthYearPicker) comp).setYearRange(new Range(y, y + 10));

                    ((CMonthYearPicker) comp).addValueValidator(new EditableValueValidator<Date>() {

                        @Override
                        public boolean isValid(CComponent<Date, ?> component, Date value) {
                            if (value == null) {
                                return true;
                            } else {
                                Date now = new Date();
                                @SuppressWarnings("deprecation")
                                Date thisMonth = new Date(now.getYear(), now.getMonth(), 1);
                                return value.compareTo(thisMonth) >= 0;
                            }
                        }

                        @Override
                        public String getValidationMessage(CComponent<Date, ?> component, Date value) {
                            return i18n.tr("Card expiry should be a future date");
                        }
                    });
                }
                return comp;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                get(proto().cardNumber()).addValueValidator(new CreditCardNumberValidator());
            }
        };
    }

    class CheckPanel extends FlexTable {

        CEntityEditor<EcheckInfo> entityComponent;

        private int index = 0;

        public CheckPanel(CEntityEditor<EcheckInfo> entityComponent) {
            this.entityComponent = entityComponent;
            setWidget(1, 0, new Image(VistaImages.INSTANCE.chequeGuide()));
            getFlexCellFormatter().setColSpan(1, 0, 3);
        }

        public void add(IObject<?> object, int width) {
            setWidget(0, index, entityComponent.inject(object).asWidget());
            getFlexCellFormatter().setWidth(0, index, width + "px");
            index++;
        }

    }

    class InnerPanelWidgetDecorator extends WidgetDecorator {

        public InnerPanelWidgetDecorator(CComponent<?, ?> component) {
            super(new Builder(component).labelWidth(12).componentWidth(12));
        }

    }
}
