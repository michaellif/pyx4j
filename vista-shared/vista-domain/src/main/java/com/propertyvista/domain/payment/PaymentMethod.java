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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.Customer;

@ToStringFormat("{0} - {1} {2,choice,null#|1#Preauthorized}")
public interface PaymentMethod extends IEntity {

    @Detached
    @ReadOnly
    @Owner
    @JoinColumn
    Customer customer();

    @OrderColumn
    IPrimitive<Integer> orderId();

    @NotNull
    @ToString(index = 0)
    @Caption(name = "Payment Type")
    @Editor(type = EditorType.radiogroup)
    @MemberColumn(name = "paymentType")
    IPrimitive<PaymentType> type();

    @Owned
    @ToString(index = 1)
    @Caption(name = "Payment Attributes")
    PaymentDetails details();

    // Billing Address:
    IPrimitive<Boolean> sameAsCurrent();

    @EmbeddedEntity
    AddressStructured billingAddress();

    @Editor(type = EditorType.phone)
    IPrimitive<String> phone();

    /**
     * Indicates if this method is one-time usage only and shouldn't be saved in DB.
     */
    IPrimitive<Boolean> isOneTimePayment();

    /**
     * Indicates if this method is deleted (still persists in DB but not used anymore!).
     */
    IPrimitive<Boolean> isDeleted();

    /**
     * Run-time data - used for setup of Tenant's pre-authorized payment method.
     */
    @Transient
    @ToString(index = 2)
    @Caption(name = "Use for Preauthorized Payments")
    IPrimitive<Boolean> isPreauthorized();
}
