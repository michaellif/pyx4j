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
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.web.client.ui.AbstractWizardView;

public class TenantSureOrderWizardViewImpl extends AbstractWizardView<TenantSureInsurancePolicyDTO> implements TenantSureOrderWizardView {

    private static final I18n i18n = I18n.get(TenantSureOrderWizardViewImpl.class);

    private TenantSureOrderWizard wizard;

    public TenantSureOrderWizardViewImpl() {
        super();
        setWizard(wizard = new TenantSureOrderWizard(this, i18n.tr("Submit")));

    }

    @Override
    public void setPresenter(com.propertyvista.portal.web.client.ui.IWizardView.IWizardPresenter<TenantSureInsurancePolicyDTO> presenter) {
        wizard.setPresenter((TenantSureOrderWizardPersenter) presenter);
        super.setPresenter(presenter);
    }

    @Override
    public void waitForQuote() {
        wizard.waitForQuote();
    }

    @Override
    public void setQuote(TenantSureQuoteDTO quote) {
        wizard.setQuote(quote);
    }

    @Override
    public void setBillingAddress(AddressSimple billingAddress) {
        wizard.setBillingAddress(billingAddress);
    }

    @Override
    public void onSendQuoteDetailsSucess(String email) {
        // TODO Auto-generated method stub

        // show dialog or notification that the quote has been sent to the *email*...
    }
}
