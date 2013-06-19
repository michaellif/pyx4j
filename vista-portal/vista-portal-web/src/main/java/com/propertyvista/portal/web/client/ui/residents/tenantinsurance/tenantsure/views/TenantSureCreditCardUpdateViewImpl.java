/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog.Type;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.portal.web.client.ui.residents.EditImpl;
import com.propertyvista.portal.web.client.ui.residents.ViewBase;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSurePaymentMethodForm;

public class TenantSureCreditCardUpdateViewImpl extends EditImpl<InsurancePaymentMethod> implements TenantSureCreditCardUpdateView {

    private static final I18n i18n = I18n.get(TenantSureCreditCardUpdateViewImpl.class);

    public TenantSureCreditCardUpdateViewImpl() {
        setForm(new TenantSurePaymentMethodForm(new Command() {
            @Override
            public void execute() {
                ((TenantSureCreditCardUpdateView.Presenter) getPresenter()).onTenantAddressRequested();
            }
        }));
    }

    @Override
    public void setPresenter(ViewBase.Presenter<InsurancePaymentMethod> presenter) {
        super.setPresenter(presenter);
        getForm().setVisited(false);
    }

    @Override
    public void reportCCUpdateSuccess() {
        MessageDialog.show("", i18n.tr("Credit card was updated sucessfully"), Type.Info, new OkOption() {

            @Override
            public boolean onClickOk() {
                ((TenantSureCreditCardUpdateView.Presenter) getPresenter()).onCCUpdateSuccessAcknowledged();
                return true;
            }

        });
    }

    @Override
    public void setTenantAddress(AddressStructured tenantAddress) {
        InsurancePaymentMethod paymentMethod = ((TenantSurePaymentMethodForm) getForm()).getValue();
        paymentMethod.billingAddress().set(tenantAddress);
        ((TenantSurePaymentMethodForm) getForm()).setValue(paymentMethod);
    }

    @Override
    public void setPreAuthorizedDebitAgreement(String agreementTextHml) {
        ((TenantSurePaymentMethodForm) getForm()).setPreAuthorizedAgreement(agreementTextHml);
    }

    @Override
    protected void onSubmit() {
        getForm().revalidate();
        getForm().setUnconditionalValidationErrorRendering(true);
        if (getForm().isValid() & ((TenantSurePaymentMethodForm) getForm()).isAgreedToPreauthorizedPayments()) {
            super.onSubmit();
        } else {
            MessageDialog.info("You must fill out the form and agree to pre-authorized debit agreement to continue");
        }
    }
}
