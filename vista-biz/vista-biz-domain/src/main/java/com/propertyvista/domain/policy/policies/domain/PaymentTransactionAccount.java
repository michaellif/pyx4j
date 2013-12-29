/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.domain;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;

import com.propertyvista.domain.financial.PaymentTransactionType;
import com.propertyvista.domain.policy.policies.PaymentTransactionsPolicy;

public interface PaymentTransactionAccount extends IEntity {

    @Caption(name = "Merchant's Terminal ID")
    IPrimitive<String> terminalID();

    @NotNull
    @Caption(name = "Merchant's bank")
    IPrimitive<String> bankId();

    @NotNull
    @Caption(name = "Merchant's Transit Number")
    IPrimitive<Integer> transitNo();

    @NotNull
    @Caption(name = "Merchant's Account Number")
    IPrimitive<String> accountNo();

    IPrimitiveSet<PaymentTransactionType> allowedTransaction();

    // internals

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    PaymentTransactionsPolicy policy();

    @OrderColumn
    IPrimitive<Integer> orderInPolicy();

}
