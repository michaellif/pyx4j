/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.profile.paymentmethods;

import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.payments.EcheckInfoEditor;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.financial.RevealAccountNumberService;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class PmcPaymentMethodFolder extends VistaBoxFolder<PmcPaymentMethod> {

    private static final I18n i18n = I18n.get(PmcPaymentMethodFolder.class);

    public class PmcPaymentMethodEditor extends PaymentMethodEditor<PmcPaymentMethod> {

        public PmcPaymentMethodEditor() {
            super(PmcPaymentMethod.class);
        }

        @Override
        public Set<PaymentType> defaultPaymentTypes() {
            return EnumSet.of(PaymentType.CreditCard);
        }

        @Override
        public IsWidget createContent() {
            IsWidget content = super.createContent();

            // tune-up:
            setPaymentTypeSelectionVisible(false);
            setIsPreauthorizedVisible(true);
            setBillingAddressVisible(false);

            return content;
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().selectForEquifaxPayments()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue().booleanValue()) {
                        for (int i = 0; i < PmcPaymentMethodFolder.this.getItemCount(); ++i) {
                            for (CComponent<?> comp : PmcPaymentMethodFolder.this.getItem(i).getComponents()) {
                                if (comp instanceof PmcPaymentMethodEditor && !comp.equals(PmcPaymentMethodEditor.this)) {
                                    ((PmcPaymentMethodEditor) comp).get(proto().selectForEquifaxPayments()).setValue(false, false);
                                }
                            }
                        }
                    }
                }
            });
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
    }

    public PmcPaymentMethodFolder() {
        super(PmcPaymentMethod.class);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PmcPaymentMethod) {
            return new PmcPaymentMethodEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        PmcPaymentMethod pmcPaymentMethod = EntityFactory.create(PmcPaymentMethod.class);
        pmcPaymentMethod.type().setValue(PaymentType.CreditCard);
        pmcPaymentMethod.details().set(EntityFactory.create(CreditCardInfo.class));
        addItem(pmcPaymentMethod);
    }
}
