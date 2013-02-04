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
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog.Type;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.portal.client.ui.residents.BasicViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSurePaymentMethodForm;

public class TenantSureCreditCardUpdateViewImpl extends BasicViewImpl<InsurancePaymentMethod> implements TenantSureCreditCardUpdateView {

    private static final I18n i18n = I18n.get(TenantSureCreditCardUpdateViewImpl.class);

    public TenantSureCreditCardUpdateViewImpl() {
        setForm(new TenantSurePaymentMethodForm(new Command() {
            @Override
            public void execute() {
                ((TenantSureCreditCardUpdateView.Presenter) presenter).onTenantAddressRequested();
            }
        }));
    }

    @Override
    public void reportCCUpdateSuccess() {
        MessageDialog.show("", i18n.tr("Credit card was updated sucessfully"), Type.Info, new OkOption() {

            @Override
            public boolean onClickOk() {
                ((TenantSureCreditCardUpdateView.Presenter) presenter).onCCUpdateSuccessAcknowledged();
                return true;
            }

        });
    }

    @Override
    public void setTenantAddress(AddressStructured tenantAddress) {
        InsurancePaymentMethod paymentMethod = ((TenantSurePaymentMethodForm) form).getValue();
        paymentMethod.billingAddress().set(tenantAddress);
        ((TenantSurePaymentMethodForm) form).setValue(paymentMethod);
    }
}
