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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.CashInfo;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class PaymentMethodEditor extends CEntityDecoratableEditor<PaymentMethod> {

    private static final I18n i18n = I18n.get(PaymentMethodEditor.class);

    private final SimplePanel paymentDetailsHolder = new SimplePanel();

    private CEntityEditor<AddressStructured> billingAddress;

    private Widget billingAddressHeader;

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
                return PaymentType.avalableInCrm();
            }
        }), 25).build());

        main.setH3(++row, 0, 1, proto().details().getMeta().getCaption());
        main.setWidget(++row, 0, paymentDetailsHolder);

        main.setH3(++row, 0, 1, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = main.getWidget(row, 0);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        main.setWidget(++row, 0, inject(proto().billingAddress(), billingAddress = new AddressStructuredEditor(true)));

        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().phone()), 12).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isDefault())).build());
        get(proto().isDefault()).setVisible(false);

        // tweak UI:
        get(proto().type()).addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                selectPaymentDetailsEditor(event.getValue());
            }
        });

        get(proto().sameAsCurrent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onBillingAddressSameAsCurrentOne(event.getValue(), billingAddress);
                billingAddress.setEditable(!event.getValue());
            }
        });

        main.setWidth("100%");
        return main;
    }

    @Override
    protected void propagateValue(PaymentMethod value, boolean fireEvent, boolean populate) {
        selectPaymentDetailsEditor(value != null ? value.type().getValue() : null);
        super.propagateValue(value, fireEvent, populate);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().sameAsCurrent()).setVisible(!isViewable());
    }

    public void selectPaymentDetailsEditor(PaymentType type) {

        if (this.contains(proto().details())) {
            this.unbind(proto().details());
            paymentDetailsHolder.setWidget(null);
        }

        if (type != null && getValue() != null) {
            CEntityEditor editor = null;
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
            }

            if (editor != null) {
                this.inject(proto().details(), editor);
                editor.populate(details.cast());

                paymentDetailsHolder.setWidget(editor);
            }
        }
    }

    // some UI tuning mechanics for client:

    public void setTypeSelectionVisible(boolean visible) {
        get(proto().type()).setVisible(visible);
    }

    public boolean isTypeSelectionVisible() {
        return get(proto().type()).isVisible();
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

    public void setIsDefaultVisible(boolean visible) {
        get(proto().isDefault()).setVisible(visible);

    }

    public boolean isIsDefaultVisible() {
        return get(proto().isDefault()).isVisible();
    }

    protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured, ?> comp) {
        // Implements meaningful in derived classes...
    }
}
