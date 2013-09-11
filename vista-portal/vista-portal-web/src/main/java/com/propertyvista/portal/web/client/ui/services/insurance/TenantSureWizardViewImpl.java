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
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureAgreementDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.web.client.ui.AbstractWizard;

public class TenantSureWizardViewImpl extends AbstractWizard<TenantSureAgreementDTO> implements TenantSureWizardView {

    private static final I18n i18n = I18n.get(TenantSureWizardViewImpl.class);

    public TenantSureWizardViewImpl() {
        super();
        setWizard(new TenantSureWizardForm(this, i18n.tr("Submit")));

    }

    @Override
    public void waitForQuote() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setQuote(TenantSureQuoteDTO quote) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBillingAddress(AddressSimple billingAddress) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendQuoteDetailsSucess(String email) {
        // TODO Auto-generated method stub

        // show dialog or notification that the quote has been sent to the *email*...
    }
}
