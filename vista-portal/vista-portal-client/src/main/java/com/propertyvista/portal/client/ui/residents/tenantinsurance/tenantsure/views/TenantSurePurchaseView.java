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
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteSummaryDTO;

public interface TenantSurePurchaseView extends IsWidget {

    interface Presenter {

        void onCoverageRequestChanged();

        void onQuoteAccepted();

        void cancel();
    }

    void setPresenter(Presenter presenter);

    /** resets the view with new parameters */
    void init(TenantSureQuotationRequestParamsDTO quotationRequestParams);

    void setQuote(TenantSureQuoteSummaryDTO quote);

    void waitForQuote();

    void waitForPaymentProcessing();

    void populatePaymentProcessingError(String errorReason);

    TenantSureQuotationRequestDTO getCoverageRequest();

    /** @return <code>null</code> when there's no accepted quote, or accepted quote */
    TenantSureQuoteSummaryDTO getAcceptedQuote();

    CreditCardInfo getCreditCardInfo();
}
