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

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceTenantSureCertificateDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.web.client.ui.IWizardView;

public interface TenantSureOrderWizardView extends IWizardView<InsuranceTenantSureCertificateDTO> {

    interface TenantSureOrderWizardPersenter extends WizardPresenter<InsuranceTenantSureCertificateDTO> {

        void sendQuoteDetailsEmail();

        void getNewQuote();

        void populateCurrentAddressAsBillingAddress();

    }

    void waitForQuote();

    void setQuote(TenantSureQuoteDTO quote);

    void setBillingAddress(AddressSimple billingAddress);

    void onSendQuoteDetailsSucess(String email);

}
