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
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.policy.framework.BuildingPolicy;
import com.propertyvista.domain.policy.policies.domain.PaymentTransactionAccount;

/**
 * Pre-authorized debit transactions and credit card transaction policy
 */
@DiscriminatorValue("PaymentTransactionsPolicy")
public interface PaymentTransactionsPolicy extends BuildingPolicy {

    @Owned
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PaymentTransactionAccount.OrderId.class)
    IList<PaymentTransactionAccount> policyItems();

}
