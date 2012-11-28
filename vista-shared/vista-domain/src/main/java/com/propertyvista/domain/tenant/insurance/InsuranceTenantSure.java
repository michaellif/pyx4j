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
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.insurance;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

/**
 * TenantSure operational data, updated by processes.
 * Created when insurance is approved and bound
 */
public interface InsuranceTenantSure extends IEntity {

    public enum Status {

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

    @Owner
    @JoinColumn
    InsuranceTenantSureClient client();

    InsuranceCertificate insuranceCertificate();

    /**
     * PK in TenantSure API of the accepted quote that was bound to the owner client/tenant.
     */
    @NotNull
    IPrimitive<String> quoteId();

    IPrimitive<InsuranceTenantSure.Status> status();

    IPrimitive<InsuranceTenantSure.CancellationType> cancellation();

    /** for insurance that is cancelled by TenantSure, holds the reason for cancellation */
    IPrimitive<String> cancellationDescriptionReasonFromTenantSure();

    @NotNull
    IPrimitive<LogicalDate> inceptionDate();

    IPrimitive<LogicalDate> expiryDate();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> monthlyPayable();

    @Owned
    InsuranceTenantSureDetails details();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<InsuranceTenantSureTransaction> transactions();

}
