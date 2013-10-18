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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog_v2;

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
    public void acknowledgeSendQuoteDetailsSucess(String email) {
        MessageDialog_v2.info(i18n.tr("Your quote documentation was sent to {0}", email));
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        if (caught instanceof UserRuntimeException) {
            MessageDialog_v2.error(i18n.tr("Error"), caught.getMessage());
            return true;
        } else {
            return super.onSaveFail(caught);
        }
    }
}
