/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.tenant.lease.Lease;

@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface BillingAccount extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    Lease lease();

    // TODO move to InternalBillingAccount when $asInstanceOf  implemented
    @ReadOnly(allowOverrideNull = true)
    BillingType billingType();

    @Length(14)
    @Indexed(uniqueConstraint = true)
    IPrimitive<String> accountNumber();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<PaymentRecord> payments();

}
