/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.common;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public abstract class PaymentMethodFolder extends VistaBoxFolder<LeasePaymentMethod> {

    private static final I18n i18n = I18n.get(PaymentMethodFolder.class);

    private final boolean showPreauthorized;

    public PaymentMethodFolder(boolean modifiable, boolean showPreauthorized) {
        super(LeasePaymentMethod.class, modifiable);
        this.showPreauthorized = showPreauthorized;
    }

    @Override
    protected void addItem() {
        new SelectEnumDialog<PaymentType>(i18n.tr("Select Payment Method Type"), PaymentType.avalableInProfile()) {
            @Override
            public boolean onClickOk() {
                LeasePaymentMethod pm = EntityFactory.create(LeasePaymentMethod.class);
                pm.type().setValue(getSelectedType());
                addItem(pm);
                return true;
            }

            @Override
            public String defineWidth() {
                return "20em";
            }
        }.show();
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LeasePaymentMethod) {
            return new PaymentMethodEditorEx();
        }
        return super.create(member);
    }

    private class PaymentMethodEditorEx extends PaymentMethodEditor {

        @Override
        public IsWidget createContent() {
            IsWidget w = super.createContent();

            // tune-up:
            setTypeSelectionVisible(false);
            setBillingAddressVisible(false);
            setIsPreauthorizedVisible(showPreauthorized);

            return w;
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().isPreauthorized()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue().booleanValue()) {
                        for (int i = 0; i < PaymentMethodFolder.this.getItemCount(); ++i) {
                            for (CComponent<?, ?> comp : PaymentMethodFolder.this.getItem(i).getComponents()) {
                                if (comp instanceof PaymentMethodEditorEx && !comp.equals(PaymentMethodEditorEx.this)) {
                                    ((PaymentMethodEditorEx) comp).get(proto().isPreauthorized()).setValue(false, false);
                                }
                            }
                        }
                    }
                }
            });
        }

        @Override
        protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured, ?> comp) {
            PaymentMethodFolder.this.onBillingAddressSameAsCurrentOne(set, comp);
        }
    }

    protected abstract void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured, ?> comp);

//    @Override
//    public void addValidations() {
//        super.addValidations();
//
//        this.addValueValidator(new EditableValueValidator<List<PaymentMethod>>() {
//            @Override
//            public ValidationFailure isValid(CComponent<List<PaymentMethod>, ?> component, List<PaymentMethod> value) {
//                if (value != null && !value.isEmpty()) {
//                    boolean primaryFound = false;
//                    for (PaymentMethod item : value) {
//                        if (item.isDefault().isBooleanTrue()) {
//                            primaryFound = true;
//                            break;
//                        }
//                    }
//                    if (!primaryFound) {
//                        return new ValidationFailure(i18n.tr("Default payment should be selected"));
//                    }
//                }
//                return null;
//            }
//        });
//    }
}
