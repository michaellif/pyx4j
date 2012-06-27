/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import java.util.Collection;
import java.util.EnumSet;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.CashInfo;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.InteracInfo;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentMethodEditor extends CEntityDecoratableForm<PaymentMethod> {

    protected static final I18n i18n = I18n.get(PaymentMethodEditor.class);

    protected final SimplePanel paymentDetailsHolder = new SimplePanel();

    protected Widget billingAddressHeader;

    public PaymentMethodEditor() {
        super(PaymentMethod.class);
    }

    public PaymentMethodEditor(IEditableComponentFactory factory) {
        super(PaymentMethod.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CRadioGroupEnum<PaymentType>(PaymentType.class, RadioGroup.Layout.HORISONTAL) {
            @Override
            public Collection<PaymentType> getOptions() {
                return getPaymentOptions();
            }
        }), 25).build());

        main.setH3(++row, 0, 1, proto().details().getMeta().getCaption());
        main.setWidget(++row, 0, paymentDetailsHolder);

        main.setH3(++row, 0, 1, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = main.getWidget(row, 0);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent())).build());
        main.setWidget(++row, 0, inject(proto().billingAddress(), new AddressStructuredEditor(true)));

        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().phone()), 12).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isPreauthorized())).build());
        get(proto().isPreauthorized()).setVisible(false);

        // tweak UI:
        get(proto().type()).addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                selectPaymentDetailsEditor(event.getValue(), false);
            }
        });

        get(proto().sameAsCurrent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onBillingAddressSameAsCurrentOne(event.getValue(), get(proto().billingAddress()));
                get(proto().billingAddress()).setEditable(!event.getValue());
            }
        });

        return main;
    }

    @Override
    protected void propagateValue(PaymentMethod value, boolean fireEvent, boolean populate) {
        selectPaymentDetailsEditor(value != null ? value.type().getValue() : null, populate);
        super.propagateValue(value, fireEvent, populate);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().sameAsCurrent()).setVisible(!isViewable());
        get(proto().billingAddress()).setEditable(!getValue().sameAsCurrent().isBooleanTrue());
    }

    @SuppressWarnings("unchecked")
    protected void selectPaymentDetailsEditor(PaymentType type, boolean populate) {

        if (this.contains(proto().details())) {
            this.unbind(proto().details());
            paymentDetailsHolder.setWidget(null);
        }

        if (populate) {
            get(proto().type()).populate(type);
        } else {
            get(proto().type()).setValue(type, false);
        }

        if (type != null && getValue() != null) {
            @SuppressWarnings("rawtypes")
            CEntityForm editor = null;
            PaymentDetails details = getValue().details();

            switch (type) {
            case Cash:
                editor = new CashInfoEditor();
                if (details.getInstanceValueClass() != CashInfo.class) {
                    details.set(EntityFactory.create(CashInfo.class));
                }
                setBillingAddressVisible(false);
                break;
            case Check:
                editor = new CheckInfoEditor();
                if (details.getInstanceValueClass() != CheckInfo.class) {
                    details.set(EntityFactory.create(CheckInfo.class));
                }
                setBillingAddressVisible(true);
                break;
            case Echeck:
                editor = new EcheckInfoEditor();
                if (details.getInstanceValueClass() != EcheckInfo.class) {
                    details.set(EntityFactory.create(EcheckInfo.class));
                }
                setBillingAddressVisible(true);
                break;
            case CreditCard:
                editor = new CreditCardInfoEditor();
                if (details.getInstanceValueClass() != CreditCardInfo.class) {
                    details.set(EntityFactory.create(CreditCardInfo.class));
                }
                setBillingAddressVisible(true);
                break;
            case Interac:
                editor = new InteracInfoEditor();
                if (details.getInstanceValueClass() != InteracInfo.class) {
                    details.set(EntityFactory.create(InteracInfo.class));
                }
                setBillingAddressVisible(false);
                break;
            }

            if (editor != null) {
                this.inject(proto().details(), editor);
                editor.populate(details.cast());
                paymentDetailsHolder.setWidget(editor);
            }
        }
    }

    public void initNew(PaymentType type) {
        PaymentMethod value = EntityFactory.create(PaymentMethod.class);
        value.type().setValue(type);
        populate(value);
    }

    // some UI tuning mechanics for client:

    public void setTypeSelectionVisible(boolean visible) {
        get(proto().type()).setVisible(visible);
    }

    public boolean isTypeSelectionVisible() {
        return get(proto().type()).isVisible();
    }

    public void setTypeSelectionEnabled(boolean visible) {
        get(proto().type()).setEnabled(visible);
    }

    public boolean isTypeSelectionEnabled() {
        return get(proto().type()).isEnabled();
    }

    public void setBillingAddressVisible(boolean visible) {
        get(proto().billingAddress()).setVisible(visible);
        get(proto().sameAsCurrent()).setVisible(visible);
        billingAddressHeader.setVisible(visible);
    }

    public boolean isBillingAddressVisible() {
        return get(proto().billingAddress()).isVisible();
    }

    public void setBillingAddressAsCurrentVisible(boolean visible) {
        get(proto().sameAsCurrent()).setVisible(visible);
    }

    public boolean isBillingAddressAsCurrentVisible() {
        return get(proto().sameAsCurrent()).isVisible();
    }

    public void setBillingAddressAsCurrentEnabled(boolean visible) {
        get(proto().sameAsCurrent()).setEnabled(visible);
    }

    public boolean isBillingAddressAsCurrentEnabled() {
        return get(proto().sameAsCurrent()).isEnabled();
    }

    public void setIsPreauthorizedVisible(boolean visible) {
        get(proto().isPreauthorized()).setVisible(visible);

    }

    public boolean isIsPreauthorizedVisible() {
        return get(proto().isPreauthorized()).isVisible();
    }

    public Collection<PaymentType> getPaymentOptions() {
        return EnumSet.allOf(PaymentType.class);
    }

    protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured, ?> comp) {
        // Implements meaningful in derived classes...
    }

    public void addTypeSelectionValueChangeHandler(ValueChangeHandler<PaymentType> handler) {
        get(proto().type()).addValueChangeHandler(handler);
    }
}
