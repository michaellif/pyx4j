/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTaxGrossPremium;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTaxUnderwriterFee;

/**
 * This entity contains information that is interesting for tenant and that is mostly retrieved from <code>retrieveCodeInformation</code>.
 * 
 * {@link https://api.cfcprograms.com/cfc_api.asmx?op=retrieveQuoteInformation}
 */
@Transient
public interface TenantSureQuoteDTO extends IEntity {

    IPrimitive<String> quoteId();

    /** holds the requested parameters that have been used to create this quote */
    TenantSureCoverageDTO coverage();

    /** annual gross premium */
    @Format("#,##0.00")
    IPrimitive<BigDecimal> grossPremium();

    IList<InsuranceTenantSureTaxGrossPremium> grossPremiumTaxBreakdown();

    /** this is gross premium plus gross premium tax (doesn't include underwriter fee) */
    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalPayable();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> underwriterFee();

    IList<InsuranceTenantSureTaxUnderwriterFee> underwriterFeeTaxBreakdown();

    // TODO here must be taxes that apply to underwriter's fee too

    /** if this field is not <code>null</code> then automatic quote is not available through the CFC system, and it will hold a message that should be displayed */
    IPrimitive<String> specialQuote();

}
