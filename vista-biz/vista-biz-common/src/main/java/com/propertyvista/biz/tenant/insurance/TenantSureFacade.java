/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public interface TenantSureFacade {

    /** @return <code>null</code> if tenant is not covered by tenant sure, or details of coverage */
    TenantSureInsuranceStatusDTO getStatus(Tenant tenantId);

    TenantSureQuoteDTO getQuote(TenantSureCoverageDTO coverage, Tenant tenantId);

    /**
     * Save all data including CC and perform TenantSure bind.
     * If any fails
     * - Save Quote in DB as InsuranceTenantSure Status.Pending
     * - Save CC, create token
     * - make payment Authorize
     * - TenantSure bind
     * - make payment Process (or Return if bind fails)
     * - Make InsuranceTenantSure.status Active
     * 
     * If any action failed: status set to Failed and record is kept in DB
     */
    void buyInsurance(TenantSureQuoteDTO quote, Tenant tenantId, String tenantName, String tenantPhone);

    /**
     * Sends a cancellation request (cancellation itself should happen on expiry date)
     * 
     * @param policyId
     * @param cancellationType
     * @param toAddress
     */
    void scheduleCancelByTenant(Tenant tenantId);

    void cancelByTenantSure(Tenant tenantId, String cancellationReason, LogicalDate expiryDate);

    void startCancellationDueToSkippedPayment(Tenant tenantId);

    void reverseCancellationDueToSkippedPayment(Tenant tenantId);

    void cancelDueToSkippedPayment(Tenant tenantId);

    void reinstate(Tenant tenantId);

    InsurancePaymentMethod getPaymentMethod(Tenant tenantId);

    /**
     * Only update credit card, do not perform outstanding payment
     */
    InsurancePaymentMethod savePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId);

    InsurancePaymentMethod updatePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId);

    /** Sends a bound insurance, returns email that has been used */
    String sendCertificate(Tenant tenantId, String email);

    /** Sends a quote */
    String sendQuote(Tenant tenantId, String quoteId);

}
