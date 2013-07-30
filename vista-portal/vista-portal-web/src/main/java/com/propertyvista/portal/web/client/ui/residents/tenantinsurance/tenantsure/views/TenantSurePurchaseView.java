/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views;

import com.pyx4j.site.client.IsView;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public interface TenantSurePurchaseView extends IsView {

    // TODO rename methods from "on" to action name...
    interface Presenter {

        // TODO rename to requestQuote
        void onCoverageRequestChanged();

        // TODO rename to populateCurrentAddressAsBillingAddress
        void onBillingAddressSameAsCurrentSelected();

        // TODO rename to acceptQuoteAndPurchaseInsurance()
        void onQuoteAccepted();

        // TODO rename acknowledgeInsurancePurchaseSuccess()
        void onPaymentProcessingSuccessAccepted();

        void cancel();

        void sendQuoteDetails(String value);

    }

    void setPresenter(Presenter presenter);

    /** resets the view with new parameters */
    void init(TenantSureQuotationRequestParamsDTO quotationRequestParams, InsurancePaymentMethod initialPaymentMethod);

    /** render view in the maintenance mode */
    void setTenantSureOnMaintenance(String message);

    void setQuote(TenantSureQuoteDTO quote);

    void waitForQuote();

    void waitForPaymentProcessing();

    void populatePaymentProcessingError(String errorReason);

    void populatePaymentProcessingSuccess();

    void populateSendQuoteDetailSuccess(String email);

    void setBillingAddress(AddressSimple billingAddress);

    TenantSureCoverageDTO getCoverageRequest();

    /** @return returns the quote that was selected by the view, or <code>null</code> when there's no accepted quote */
    TenantSureQuoteDTO getAcceptedQuote();

    InsurancePaymentMethod getPaymentMethod();

    void reportError(String message);

}
