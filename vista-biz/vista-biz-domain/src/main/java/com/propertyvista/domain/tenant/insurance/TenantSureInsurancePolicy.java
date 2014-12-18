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
 */
package com.propertyvista.domain.tenant.insurance;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;

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
        Cancelled,

        /** Insurance moved to another PMC */
        Moved;

    }

    public enum CancellationType {

        SkipPayment,

        CancelledByTenant,

        CancelledByTenantSure,

        Renewed;
    }

    @ReadOnly
    @MemberColumn(notNull = true)
    TenantSureInsurancePolicyClient client();

    /**
     * PK in TenantSure API of the accepted quote that was bound to the owner client/tenant.
     */
    @MemberColumn(notNull = true)
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
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> annualPremium();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> underwriterFee();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> brokerFee();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalAnnualTax();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalAnnualPayable();

    /** this is the amount that gets charged first on enrollment */
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalFirstPayable();

    /** this is the amount that gets charged on anniversary of enrollment instead of totalMonthlyPayable */
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalAnniversaryFirstMonthPayable();

    /** this is an amount that gets charged every month (i.e. monthly premium + the taxes) */
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalMonthlyPayable();

//    @NotNull
//    @Format("#,##0.00")
//    @Editor(type = EditorType.money)
//    IPrimitive<BigDecimal> liabilityCoverage();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> contentsCoverage();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> deductible();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<TenantSureTransaction> transactions();

    interface RenewalOfColumnId extends ColumnId {
    }

    @JoinColumn(RenewalOfColumnId.class)
    @Detached(level = AttachLevel.IdOnly)
    TenantSureInsurancePolicy renewalOf();

    @JoinTable(value = TenantSureInsurancePolicy.class, mappedBy = RenewalOfColumnId.class)
    @Detached(level = AttachLevel.Detached)
    TenantSureInsurancePolicy renewal();

    @Owned
    TenantSureCoverage coverage();

    @Owned
    @Detached
    @NotNull
    @Caption(name = "I agree to the Terms")
    @MemberColumn(name = "signature")
    CustomerSignature personalDisclaimerSignature();

}
