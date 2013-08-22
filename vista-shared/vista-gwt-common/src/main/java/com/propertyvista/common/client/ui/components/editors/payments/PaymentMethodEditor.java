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
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroup;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.CashInfo;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.InteracInfo;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;

public class PaymentMethodEditor<E extends AbstractPaymentMethod> extends CEntityDecoratableForm<E> {

    private static final I18n i18n = I18n.get(PaymentMethodEditor.class);

    protected final SimplePanel paymentDetailsHolder = new SimplePanel();

    protected Widget paymentDetailsHeader = new Label();

    protected Widget billingAddressHeader = new Label();

    protected Class<E> paymentEntityClass;

    public PaymentMethodEditor(Class<E> clazz) {
        super(clazz);
        this.paymentEntityClass = clazz;
    }

    public PaymentMethodEditor(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
        this.paymentEntityClass = clazz;
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = -1;

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().creationDate()), 10).build());

        main.setWidget(
                ++row,
                0,
                new FormDecoratorBuilder(inject(proto().type(), new CRadioGroupEnum<PaymentType>(PaymentType.class, defaultPaymentTypes(),
                        RadioGroup.Layout.HORISONTAL)), 35).build());
        if (proto() instanceof PaymentMethod) {
            main.setWidget(row, 1, new FormDecoratorBuilder(inject(((PaymentMethod) proto()).createdBy(), new CEntityLabel<AbstractPmcUser>()), 22).build());
        }

        main.setH3(++row, 0, 2, proto().details().getMeta().getCaption());
        paymentDetailsHeader = main.getWidget(row, 0);
        main.setWidget(++row, 0, 2, paymentDetailsHolder);

        main.setH3(++row, 0, 2, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = main.getWidget(row, 0);
        main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().sameAsCurrent()), 5, true).build());
        main.setWidget(++row, 0, 2, inject(proto().billingAddress(), new AddressSimpleEditor(false)));

        if (paymentEntityClass.equals(PmcPaymentMethod.class)) {
            main.setBR(++row, 0, 2);
            main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(((PmcPaymentMethod) proto()).selectForEquifaxPayments()), 5, true).build());
            get(((PmcPaymentMethod) proto()).selectForEquifaxPayments()).setVisible(false);
        }

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
    public void onReset() {
        super.onReset();
        if (get(proto().type()).isEditable() && get(proto().type()) instanceof CRadioGroup) {
            ((CRadioGroup<PaymentType>) get(proto().type())).setOptionsEnabled(EnumSet.allOf(PaymentType.class), true);
        }
        get(proto().type()).setNote(null);
        setBillingAddressVisible(false);

        // setup CRM only block:
        if (isBound(proto().id())) {
            get(proto().id()).setVisible(false);
            get(proto().creationDate()).setVisible(false);
            if (proto() instanceof PaymentMethod) {
                get(((PaymentMethod) proto()).createdBy()).setVisible(false);
            }
        }
    }

    @Override
    protected void onValuePropagation(E value, boolean fireEvent, boolean populate) {
        selectPaymentDetailsEditor(value != null ? value.type().getValue() : null, populate);
        super.onValuePropagation(value, fireEvent, populate);
    }

    @Override
    protected E preprocessValue(E value, boolean fireEvent, boolean populate) {
        if (!isValueEmpty()) {
            return super.preprocessValue(value, fireEvent, populate);
        }
        return value;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().billingAddress()).setEditable(!getValue().sameAsCurrent().isBooleanTrue());

        paymentDetailsHeader.setVisible(this.contains(proto().details()));

        // setup CRM only block:
        if (isBound(proto().id())) {
            get(proto().id()).setVisible(!getValue().id().isNull());
            get(proto().creationDate()).setVisible(!getValue().creationDate().isNull());
            if (proto() instanceof PaymentMethod) {
                get(((PaymentMethod) proto()).createdBy()).setVisible(!((PaymentMethod) getValue()).createdBy().isNull());
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void selectPaymentDetailsEditor(PaymentType type, boolean populate) {

        if (this.contains(proto().details())) {
            this.unbind(proto().details());
            setPaymentDetailsWidget(null);
        }

        if (populate) {
            get(proto().type()).populate(type);
        } else {
            get(proto().type()).setValue(type, false);
        }

        setBillingAddressVisible(false);

        if (type != null && getValue() != null) {
            @SuppressWarnings("rawtypes")
            CEntityForm editor = null;
            PaymentDetails details = getValue().details();

            switch (type) {
            case Cash:
                // Disable cash editor:            
//                editor = createCashInfoEditor();
                if (details.getInstanceValueClass() != CashInfo.class) {
                    details.set(EntityFactory.create(CashInfo.class));
                }
                break;
            case Check:
                editor = createCheckInfoEditor();
                if (details.getInstanceValueClass() != CheckInfo.class) {
                    details.set(EntityFactory.create(CheckInfo.class));
                }
                if (details.getPrimaryKey() == null) {
                    details.<CheckInfo> cast().nameOn().setValue(getNameOn());
                }
                setBillingAddressVisible(true);
                break;
            case Echeck:
                editor = createEcheckInfoEditor();
                if (details.getInstanceValueClass() != EcheckInfo.class) {
                    details.set(EntityFactory.create(EcheckInfo.class));
                }
                if (details.getPrimaryKey() == null) {
                    details.<EcheckInfo> cast().nameOn().setValue(getNameOn());
                }
                setBillingAddressVisible(true);
                break;
            case CreditCard:
                editor = createCreditCardInfoEditor();
                if (details.getInstanceValueClass() != CreditCardInfo.class) {
                    details.set(EntityFactory.create(CreditCardInfo.class));
                }
                if (details.getPrimaryKey() == null) {
                    details.<CreditCardInfo> cast().nameOn().setValue(getNameOn());
                }
                setBillingAddressVisible(true);
                break;
            case Interac:
                editor = createInteracInfoEditor();
                if (details.getInstanceValueClass() != InteracInfo.class) {
                    details.set(EntityFactory.create(InteracInfo.class));
                }
                break;
            case DirectBanking:
                setPaymentDetailsWidget(null);
                break;
            default:
                break;
            }

            if (editor != null) {
                inject(proto().details(), editor);
                editor.populate(details.cast());
                setPaymentDetailsWidget(editor.asWidget());
            }
        }
    }

    // Override these methods for method editors customization:

    protected CEntityForm<?> createCashInfoEditor() {
        return new CashInfoEditor();
    }

    protected CEntityForm<?> createCheckInfoEditor() {
        return new CheckInfoEditor();
    }

    protected CEntityForm<?> createEcheckInfoEditor() {
        return new EcheckInfoEditor();
    }

    protected CEntityForm<?> createCreditCardInfoEditor() {
        return new CreditCardInfoEditor() {
            @Override
            protected Set<CreditCardType> getAllowedCardTypes() {
                return PaymentMethodEditor.this.getAllowedCardTypes();
            }
        };
    }

    protected CEntityForm<?> createInteracInfoEditor() {
        return new InteracInfoEditor();
    }

    protected String getNameOn() {
        return null;
    }

    private void setPaymentDetailsWidget(Widget w) {
        paymentDetailsHolder.setWidget(w);

        paymentDetailsHolder.setVisible(w != null);
        paymentDetailsHeader.setVisible(w != null);
    }

    public void initNew(PaymentType type) {
        E value = EntityFactory.create(paymentEntityClass);
        value.type().setValue(type);
        populate(value);
    }

    // some UI tuning mechanics for client:

    public void setPaymentTypeSelectionVisible(boolean visible) {
        get(proto().type()).setVisible(visible);
    }

    public boolean isPaymentTypeSelectionVisible() {
        return get(proto().type()).isVisible();
    }

    public void setPaymentTypeSelectionEditable(boolean enabled) {
        get(proto().type()).setEditable(enabled);
    }

    public boolean isPaymentTypeSelectionEditable() {
        return get(proto().type()).isEditable();
    }

    public void setBillingAddressVisible(boolean visible) {
        get(proto().billingAddress()).setVisible(visible);
        get(proto().sameAsCurrent()).setVisible(visible);
        billingAddressHeader.setVisible(visible);
    }

    public boolean isBillingAddressVisible() {
        return get(proto().billingAddress()).isVisible();
    }

    public void setBillingAddressAsCurrentEnabled(boolean visible) {
        get(proto().sameAsCurrent()).setEnabled(visible);
    }

    public boolean isBillingAddressAsCurrentEnabled() {
        return get(proto().sameAsCurrent()).isEnabled();
    }

    /**
     * In case of PmcPaymentmethod enables the checkbox for activating the payment method, in LeasePayment method controls the visibility of isPreauthorized()
     */
    public void setIsPreauthorizedVisible(boolean visible) {
        if (paymentEntityClass.equals(PmcPaymentMethod.class)) {
            get(((PmcPaymentMethod) proto()).selectForEquifaxPayments()).setVisible(visible);
        }
    }

    public boolean isIsPreauthorizedVisible() {
        if (paymentEntityClass.equals(PmcPaymentMethod.class)) {
            return get(((PmcPaymentMethod) proto()).selectForEquifaxPayments()).isVisible();
        } else {
            return false;
        }
    }

    // Payment Type options manipulation:

    /**
     * Override in derived classes to supply alternative set of options.
     */
    public Set<PaymentType> defaultPaymentTypes() {
        return EnumSet.allOf(PaymentType.class);
    }

    public void setPaymentTypes(Collection<PaymentType> types) {
        ((CRadioGroup<PaymentType>) get(proto().type())).setOptions(types);
    }

    public void setPaymentTypesEnabled(Collection<PaymentType> opt, boolean enabled) {
        if (get(proto().type()).isEditable() && get(proto().type()) instanceof CRadioGroup) {
            ((CRadioGroup<PaymentType>) get(proto().type())).setOptionsEnabled(opt, enabled);
        }
    }

    public void setElectronicPaymentsEnabled(Boolean electronicPaymentsEnabled) {
        if (electronicPaymentsEnabled != Boolean.TRUE) {
            this.setPaymentTypesEnabled(PaymentType.electronicPayments(), false);
            (get(proto().type())).setNote(i18n.tr("Warning: Building has not been set up to process electronic payments yet"), NoteStyle.Warn);
        }
    }

    protected Set<CreditCardType> getAllowedCardTypes() {
        return EnumSet.allOf(CreditCardType.class);
    }

    protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressSimple> comp) {
        // Implements meaningful in derived classes...
    }

    public void addTypeSelectionValueChangeHandler(ValueChangeHandler<PaymentType> handler) {
        get(proto().type()).addValueChangeHandler(handler);
    }
}
