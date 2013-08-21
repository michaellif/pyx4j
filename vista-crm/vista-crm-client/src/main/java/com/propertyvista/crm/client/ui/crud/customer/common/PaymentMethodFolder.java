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

import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.financial.RevealAccountNumberService;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.VistaCrmBehavior;

public abstract class PaymentMethodFolder extends VistaBoxFolder<LeasePaymentMethod> {

    private static final I18n i18n = I18n.get(PaymentMethodFolder.class);

    public PaymentMethodFolder(boolean modifiable) {
        super(LeasePaymentMethod.class, modifiable);
    }

    @Override
    protected void addItem() {
        getAllowedPaymentTypes(new DefaultAsyncCallback<EnumSet<PaymentType>>() {
            @Override
            public void onSuccess(EnumSet<PaymentType> result) {
                result.removeAll(PaymentType.notAllowedInProfile());
                new SelectEnumDialog<PaymentType>(i18n.tr("Select Payment Method Type"), result) {
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
        });
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LeasePaymentMethod) {
            return new LeasePaymentMethodEditor();
        }
        return super.create(member);
    }

    private class LeasePaymentMethodEditor extends PaymentMethodEditor<LeasePaymentMethod> {

        public LeasePaymentMethodEditor() {
            super(LeasePaymentMethod.class);
        }

        @Override
        public IsWidget createContent() {
            IsWidget content = super.createContent();

            // tune-up:
            setPaymentTypeSelectionVisible(!isEditable());
            setBillingAddressVisible(false);

            return content;
        }

        @Override
        protected CEntityForm<?> createEcheckInfoEditor() {
            return new EcheckInfoEditor() {
                @SuppressWarnings("rawtypes")
                @Override
                public IsWidget createContent() {
                    IsWidget content = super.createContent();

                    if (SecurityController.checkBehavior(VistaCrmBehavior.Billing)) {
                        ((CField) get(proto().accountNo())).setNavigationCommand(new Command() {
                            @Override
                            public void execute() {
                                GWT.<RevealAccountNumberService> create(RevealAccountNumberService.class).obtainUnobfuscatedAccountNumber(
                                        new DefaultAsyncCallback<EcheckInfo>() {
                                            @Override
                                            public void onSuccess(EcheckInfo result) {
                                                MessageDialog.info(i18n.tr("Account Number") + ": <b>" + result.accountNo().newNumber().getStringView()
                                                        + "</b>");
                                            }
                                        }, EntityFactory.createIdentityStub(EcheckInfo.class, getValue().getPrimaryKey()));
                            }
                        });
                    }

                    return content;
                };
            };
        }

        @Override
        protected String getNameOn() {
            return PaymentMethodFolder.this.getNameOn();
        }

        @Override
        protected Set<CreditCardType> getAllowedCardTypes() {
            return PaymentMethodFolder.this.getAllowedCardTypes();
        };

        @Override
        protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressSimple> comp) {
            PaymentMethodFolder.this.onBillingAddressSameAsCurrentOne(set, comp);
        }
    }

    protected abstract String getNameOn();

    protected abstract Set<CreditCardType> getAllowedCardTypes();

    protected abstract void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressSimple> comp);

    protected abstract void getAllowedPaymentTypes(AsyncCallback<EnumSet<PaymentType>> callback);

//    @Override
//    public void addValidations() {
//        super.addValidations();
//
//        this.addValueValidator(new EditableValueValidator<List<PaymentMethod>>() {
//            @Override
//            public ValidationFailure isValid(CComponent<List<PaymentMethod>> component, List<PaymentMethod> value) {
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
