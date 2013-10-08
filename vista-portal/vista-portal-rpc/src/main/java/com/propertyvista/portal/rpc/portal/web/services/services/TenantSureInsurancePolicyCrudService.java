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
package com.propertyvista.portal.rpc.portal.web.services.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;

public interface TenantSureInsurancePolicyCrudService extends AbstractCrudService<TenantSureInsurancePolicyDTO> {

    @Deprecated
    /** TenantSure Purchase service finish should be used */
    void acceptQuote(AsyncCallback<VoidSerializable> callback, TenantSureQuoteDTO quote, String tenantName, String tenantPhone,
            InsurancePaymentMethod paymentMethod);

    void getQuote(AsyncCallback<TenantSureQuoteDTO> callback, TenantSureCoverageDTO coverageRequest);

    void getCurrentTenantAddress(AsyncCallback<AddressSimple> callback);

    void sendQuoteDetails(AsyncCallback<String> asyncCallback, String quoteId);

    // Management related methods start here:

    void getPreAuthorizedPaymentsAgreement(AsyncCallback<String> areementHtml);

    void getFaq(AsyncCallback<String> faqHtml);

    void updatePaymentMethod(AsyncCallback<VoidSerializable> callback, InsurancePaymentMethod paymentMethod);

    void cancelTenantSure(AsyncCallback<VoidSerializable> callback);

    void reinstate(AsyncCallback<VoidSerializable> callback);

    /**
     * if email is <code>null</code> will send the email to the tenant's email address, returns an email address that was used to send it.
     */
    void sendCertificate(AsyncCallback<String> defaultAsyncCallback, String email);
}
