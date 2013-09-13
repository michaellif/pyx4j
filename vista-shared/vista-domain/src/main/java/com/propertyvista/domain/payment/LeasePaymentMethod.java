/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 31, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.Customer;

@DiscriminatorValue("LeasePaymentMethod")
public interface LeasePaymentMethod extends PaymentMethod {

    @Detached
    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    Customer customer();

    @OrderColumn
    IPrimitive<Integer> orderId();

    /**
     * Indicates if this method is in profile.
     */
    @NotNull
    IPrimitive<Boolean> isProfiledMethod();
}
