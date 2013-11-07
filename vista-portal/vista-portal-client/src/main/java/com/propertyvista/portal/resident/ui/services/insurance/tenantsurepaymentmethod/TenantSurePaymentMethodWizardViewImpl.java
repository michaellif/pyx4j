/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance.tenantsurepaymentmethod;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsurancePaymentMethodDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardView;

public class TenantSurePaymentMethodWizardViewImpl extends AbstractWizardView<InsurancePaymentMethodDTO> implements TenantSurePaymentMethodWizardView {

    public TenantSurePaymentMethodWizardViewImpl() {
        super();
        setWizard(new TenantSurePaymentMethodWizard(this));
    }

    @Override
    public void setBillingAddress(AddressSimple address) {
        ((TenantSurePaymentMethodWizard) getWizard()).setBillingAddress(address);
    }

    @Override
    public void setPresenter(com.propertyvista.portal.shared.ui.IWizardView.IWizardPresenter<InsurancePaymentMethodDTO> presenter) {
        ((TenantSurePaymentMethodWizard) getWizard()).setPresenter((Persenter) presenter);
        super.setPresenter(presenter);
    }

}
