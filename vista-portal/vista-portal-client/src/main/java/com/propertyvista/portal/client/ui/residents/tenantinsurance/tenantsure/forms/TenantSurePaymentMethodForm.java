/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.resources.TenantSureResources;

public class TenantSurePaymentMethodForm extends PaymentMethodForm {

    private final Command onSameAsCurrentAddressSelected;

    public TenantSurePaymentMethodForm(Command onSameAsCurrentAddressSelected) {
        this.onSameAsCurrentAddressSelected = onSameAsCurrentAddressSelected;
    }

    public TenantSurePaymentMethodForm() {
        this(null);
    }

    @Override
    public List<PaymentType> getPaymentOptions() {
        return Arrays.asList(PaymentType.CreditCard);
    }

    @Override
    protected void loadLegalTerms(PaymentType type) {
        switch (type) {
        case CreditCard:
            legalTerms.setValue(TenantSureResources.INSTANCE.preAuthorizedPaymentDisclaimer().getText());
            break;
        default:
            assert false : type.name() + " cannot be used for TenantSure!";
            break;
        }
    }

    @Override
    protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured, ?> comp) {
        if (set) {
            onSameAsCurrentAddressSelected.execute();
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        setBillingAddressAsCurrentEnabled(onSameAsCurrentAddressSelected != null);
        setBillingAddressAsCurrentVisible(onSameAsCurrentAddressSelected != null);
        super.onValueSet(populate);
    }

}
