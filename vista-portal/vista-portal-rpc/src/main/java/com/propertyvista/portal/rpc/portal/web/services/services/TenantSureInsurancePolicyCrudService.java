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
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;

public interface TenantSureInsurancePolicyCrudService extends AbstractCrudService<TenantSureInsurancePolicyDTO> {

    @Deprecated
    void getQuotationRequestParams(AsyncCallback<TenantSureQuotationRequestParamsDTO> callback);

    void getQuote(AsyncCallback<TenantSureQuoteDTO> callback, TenantSureCoverageDTO coverageRequest);

    @Deprecated
    /** TenantSure Purchase service finish should be used */
    void acceptQuote(AsyncCallback<VoidSerializable> callback, TenantSureQuoteDTO quote, String tenantName, String tenantPhone,
            InsurancePaymentMethod paymentMethod);

    void getCurrentTenantAddress(AsyncCallback<AddressSimple> callback);

    void sendQuoteDetails(AsyncCallback<String> asyncCallback, String quoteId);
}
