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
package com.propertyvista.portal.web.client.ui.financial.paymentmethod;

import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.web.client.ui.financial.PortalPaymentTypesUtil;

public class PaymentMethodEditor<E extends AbstractPaymentMethod> extends com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor<E> {

    private static final I18n i18n = I18n.get(PaymentMethodEditor.class);

    public PaymentMethodEditor(Class<E> clazz) {
        super(clazz, new VistaEditorsComponentFactory());
    }

    @Override
    public Set<PaymentType> defaultPaymentTypes() {
        return PortalPaymentTypesUtil.getAllowedPaymentTypes(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type(), new CComboBox<PaymentType>()), 15).build());
        content.setWidget(++row, 0, paymentDetailsHolder);

        content.setH1(++row, 0, 1, proto().billingAddress().getMeta().getCaption());
        billingAddressHeader = content.getWidget(row, 0);

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sameAsCurrent()), 5).build());
        content.setWidget(++row, 0, inject(proto().billingAddress(), new AddressSimpleEditor(true, 15)));

        // tweaks:
        ((CComboBox<PaymentType>) get(proto().type())).setOptions(defaultPaymentTypes());
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

        return content;
    }

    @Override
    protected CEntityForm<?> createEcheckInfoEditor() {
        return new EcheckInfoEditor(15);
    }

    @Override
    protected CEntityForm<?> createCreditCardInfoEditor() {
        return new CreditCardInfoEditor(15) {
            @Override
            protected Set<CreditCardType> getAllowedCardTypes() {
                return PaymentMethodEditor.this.getAllowedCardTypes();
            }
        };
    }

    @Override
    public void onReset() {
        super.onReset();

        setPaymentTypeSelectionEditable(true);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (get(proto().type()).isEditable() && get(proto().type()) instanceof CComboBox) {
            // set single-available option preselected for new items: 
            CComboBox<PaymentType> type = ((CComboBox<PaymentType>) get(proto().type()));
            if (getValue().id().isNull() && !type.getOptions().isEmpty()) {
                type.setValue(type.getOptions().get(0), false, populate);
            }
            setPaymentTypeSelectionEditable(getValue().id().isNull());
        }
    }

    @Override
    protected void setPaymentDetailsWidget(Widget w) {
        super.setPaymentDetailsWidget(w);

        // hide details while editing got some types:
        PaymentType type = get(proto().type()).getValue();

        if (type != null && isEditable()) {
            switch (type) {
            case Cash:
                break;
            case Check:
                break;
            case CreditCard:
                break;
            case DirectBanking:
                setPaymentDetailsWisible(false);
                break;
            case Echeck:
                break;
            case Interac:
                break;
            default:
                break;
            }
        }
    }
}
