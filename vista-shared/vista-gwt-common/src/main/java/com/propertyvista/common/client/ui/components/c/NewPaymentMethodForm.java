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

import java.util.Collection;
import java.util.EnumSet;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.common.client.ui.components.editors.payments.CashInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.CheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.InteracInfoEditor;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.CashInfo;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.InteracInfo;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class NewPaymentMethodForm extends CEntityDecoratableForm<PaymentMethod> {

    private static final I18n i18n = I18n.get(NewPaymentMethodForm.class);

    private final FlowPanel paymentTypeImagesPanel = new FlowPanel();

    private final SimplePanel paymentDetailsHolder = new SimplePanel();

    private CEntityForm<AddressStructured> billingAddress;

    private Widget billingAddressHeader;

    private final boolean twoColumns;

    public NewPaymentMethodForm() {
        this(false);
    }

    public NewPaymentMethodForm(boolean twoColumns) {
        super(PaymentMethod.class, new VistaEditorsComponentFactory());
        this.twoColumns = twoColumns;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel container = new FormFlexPanel();
        container.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditor.name());

        int row = -1;
        container.setH1(++row, 0, 3, proto().type().getMeta().getCaption());

        paymentTypeImagesPanel.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorImages.name());
        Image paymentTypeImage;
        FlowPanel holder;

        for (PaymentType type : getPaymentOptions()) {
            switch (type) {
            case Echeck:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentACH().getSafeUri());
                break;
            case CreditCard:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentVISA().getSafeUri());
                break;
//            case Visa:
//                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentVISA().getSafeUri());
//                break;
//            case MasterCard:
//                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentMC().getSafeUri());
//                break;
//            case Discover:
//                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentDiscover().getSafeUri());
//                break;
            case Interac:
                paymentTypeImage = new Image(VistaImages.INSTANCE.paymentInterac().getSafeUri());
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
        container.setWidget(row, 1, inject(proto().type(), new CRadioGroupEnum<PaymentType>(PaymentType.class, RadioGroup.Layout.VERTICAL) {
            @Override
            public Collection<PaymentType> getOptions() {
                return getPaymentOptions();
            }
        }));

        ComplexPanel instrumentsPanel = new FlowPanel();
        instrumentsPanel.asWidget().getElement().addClassName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorForm.name());
        instrumentsPanel.add(paymentDetailsHolder);
        container.setWidget(row, 2, instrumentsPanel);

        container.setH1(++row, 0, 3, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = container.getWidget(row, 0);
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setWidget(++row, 0, inject(proto().billingAddress(), billingAddress = new AddressStructuredEditor(twoColumns)));
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setHR(++row, 0, 3);
        container.setWidget(++row, 0, new DecoratorBuilder(inject(proto().phone()), 15).build());
        container.getFlexCellFormatter().setColSpan(row, 0, 3);

        container.setWidth("100%");

        // tweaks:
        get(proto().type()).setValue(PaymentType.Echeck);
        get(proto().type()).asWidget().setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorButtons.name());
        get(proto().type()).addValueChangeHandler(new ValueChangeHandler<PaymentType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentType> event) {
                selectPaymentDetailsEditor(event.getValue());
            }
        });

        get(proto().sameAsCurrent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onBillingAddressSameAsCurrentOne(event.getValue(), get(proto().billingAddress()));
                billingAddress.setEditable(!event.getValue());
            }
        });

        return container;
    }

    @Override
    protected void propagateValue(PaymentMethod value, boolean fireEvent, boolean populate) {
        selectPaymentDetailsEditor(value != null ? value.type().getValue() : null);
        super.propagateValue(value, fireEvent, populate);
    }

    protected void selectPaymentDetailsEditor(PaymentType type) {

        if (this.contains(proto().details())) {
            this.unbind(proto().details());
            paymentDetailsHolder.setWidget(null);
        }

        get(proto().type()).populate(type);

        if (type != null && getValue() != null) {
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

    // some UI tuning mechanics for client:

    public Collection<PaymentType> getPaymentOptions() {
        return EnumSet.allOf(PaymentType.class);
    }

    public void setBillingAddressVisible(boolean visible) {
        get(proto().billingAddress()).setVisible(visible);
        get(proto().sameAsCurrent()).setVisible(visible);
        billingAddressHeader.setVisible(visible);
    }

    public boolean isBillingAddressVisible() {
        return get(proto().billingAddress()).isVisible();
    }

    public void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured, ?> comp) {
        // Implements meaningful in derived classes...
    }
}
