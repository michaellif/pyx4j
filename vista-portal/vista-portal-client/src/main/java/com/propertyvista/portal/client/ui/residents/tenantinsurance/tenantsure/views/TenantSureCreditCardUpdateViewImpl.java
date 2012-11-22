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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.client.ui.residents.BasicViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSurePaymentMethodForm;

public class TenantSureCreditCardUpdateViewImpl extends BasicViewImpl<LeasePaymentMethod> implements TenantSureCreditCardUpdateView {

    private static final I18n i18n = I18n.get(TenantSureCreditCardUpdateViewImpl.class);

    public TenantSureCreditCardUpdateViewImpl() {
        setForm(new TenantSurePaymentMethodForm());
    }

    @Override
    public void reportCCUpdateSuccess() {
        MessageDialog.info(i18n.tr("Credit card was updated sucessfully"));
    }

    @Override
    public void setTenantAddress(AddressStructured tenantAddress) {
        LeasePaymentMethod paymentMethod = ((TenantSurePaymentMethodForm) form).getValue();
        paymentMethod.billingAddress().set(tenantAddress);
        ((TenantSurePaymentMethodForm) form).setValue(paymentMethod);
    }

}
