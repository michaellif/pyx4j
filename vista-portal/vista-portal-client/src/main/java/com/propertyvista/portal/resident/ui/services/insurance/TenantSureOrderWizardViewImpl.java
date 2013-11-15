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
package com.propertyvista.portal.resident.ui.services.insurance;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardFormView;

public class TenantSureOrderWizardViewImpl extends AbstractWizardFormView<TenantSureInsurancePolicyDTO> implements TenantSureOrderWizardView {

    private static final I18n i18n = I18n.get(TenantSureOrderWizardViewImpl.class);

    private TenantSureOrderWizard wizard;

    public TenantSureOrderWizardViewImpl() {
        super();
        setWizard(wizard = new TenantSureOrderWizard(this, i18n.tr("Submit")));

    }

    @Override
    public void setPresenter(com.propertyvista.portal.shared.ui.IWizardView.IWizardFormPresenter<TenantSureInsurancePolicyDTO> presenter) {
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
        MessageDialog.info(i18n.tr("Your quote documentation was sent to {0}", email));
    }

    @Override
    public boolean onSubmittionFailed(Throwable caught) {
        if (caught instanceof UserRuntimeException) {
            MessageDialog.error(i18n.tr("Error"), caught.getMessage());
            return true;
        } else {
            return super.onSubmittionFailed(caught);
        }
    }
}
