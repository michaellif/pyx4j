/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 26, 2013
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.domain.tenant.insurance;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.security.CustomerSignature;

@DiscriminatorValue("TenantSureInsurancePolicy")
public interface TenantSureInsurancePolicy extends InsurancePolicy<TenantSureInsuranceCertificate> {

    public enum TenantSureStatus {

        Draft,

        /**
         * Initial payment or bind Failed
         */
        Failed,

        /** Initial Payment Failed */
        Pending,

        Active,

        /** Pending cancellation */
        PendingCancellation,

        /** This insurance is no longer active, and stored for archive purposes */
        Cancelled;

    }

    public enum CancellationType {

        SkipPayment,

        CancelledByTenant,

        CancelledByTenantSure;
    }

    @ReadOnly
    @MemberColumn(notNull = true)
    TenantSureInsurancePolicyClient client();

    /**
     * PK in TenantSure API of the accepted quote that was bound to the owner client/tenant.
     */
    IPrimitive<String> quoteId();

    IPrimitive<TenantSureStatus> status();

    IPrimitive<CancellationType> cancellation();

    /** for insurance that is cancelled by TenantSure, holds the reason for cancellation */
    IPrimitive<String> cancellationDescriptionReasonFromTenantSure();

    @ReadOnly(allowOverrideNull = true)
    IPrimitive<Integer> paymentDay();

    /** a date when cancellation command has been issued */
    IPrimitive<LogicalDate> cancellationDate();

    IPrimitive<TenantSurePaymentSchedule> paymentSchedule();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> annualPremium();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> underwriterFee();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalAnnualTax();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalAnnualPayable();

    /** this is the amount that gets charged first on enrollment */
    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalFirstPayable();

    /** this is the amount that gets charged on anniversary of enrollment instead of totalMonthlyPayable */
    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalAnniversaryFirstMonthPayable();

    /** this is an amount that gets charged every month (i.e. monthly premium + the taxes) */
    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalMonthlyPayable();

//    @Format("#,##0.00")
//    @NotNull
//    IPrimitive<BigDecimal> liabilityCoverage();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> contentsCoverage();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> deductible();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<TenantSureTransaction> transactions();

    @Owned
    @Detached
    @NotNull
    @Caption(name = "I agree to the Terms")
    @MemberColumn(name = "signature")
    CustomerSignature personalDisclaimerSignature();

}
