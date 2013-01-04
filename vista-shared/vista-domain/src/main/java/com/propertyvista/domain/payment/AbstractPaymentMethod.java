/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;

@AbstractEntity
public interface AbstractPaymentMethod extends IEntity {

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

    /**
     * Indicates if this method is deleted (still persists in DB but not used anymore!).
     */
    IPrimitive<Boolean> isDeleted();

}
