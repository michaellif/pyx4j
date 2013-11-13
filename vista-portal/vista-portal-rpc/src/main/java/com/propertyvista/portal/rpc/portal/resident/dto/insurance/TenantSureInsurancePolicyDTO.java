/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto.insurance;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentDTO;

@Transient
public interface TenantSureInsurancePolicyDTO extends TenantSureInsurancePolicy {

    /** used during purchase process */
    TenantSureAgreementParamsDTO agreementParams();

    /** used during purchase process */
    TenantSureCoverageDTO tenantSureCoverageRequest();

    /** used during purchase process */
    TenantSureQuoteDTO quote();

    /** used during purchase process */
    TenantSureCoverageDTO tenantSureCoverageRequestConfirmation();

    /** used during purchase process */
    TenantSureQuoteDTO quoteConfirmation();

    /** used during purchase process */
    InsurancePaymentMethod paymentMethod();

    /** used during management phase */
    IPrimitive<Boolean> isPaymentFailed();

    /** used during management phase */
    IPrimitive<Boolean> isCancelled();

    TenantSurePaymentDTO annualPaymentDetails();

    /**
     * The date of next payment can be <code>null</code> if the there's some problem with credit card, i.e. credit limit, cancelled or whatever, anything that
     * caused last payment to fail.
     */
    TenantSurePaymentDTO nextPaymentDetails();

    IList<TenantSureMessageDTO> messages();

    @NotNull
    @Caption(name = "I agree to the Terms")
    IPrimitive<Boolean> isAgreedToPersonalDisclaimer();

}
