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
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.tenant.Tenant;

public interface PaymentMethod extends IEntity {

    @Owner
    @Detached
    @ReadOnly
    Tenant tenant();

    @MemberColumn(name = "prim")
    IPrimitive<Boolean> primary();

    @Caption(name = "Payment Types")
    @Editor(type = EditorType.radiogroup)
    @MemberColumn(name = "paymentType")
    IPrimitive<PaymentType> type();

    @Caption(name = "eCheque")
    EcheckInfo echeck();

    CreditCardInfo creditCard();

    InteracInfo interac();

    // Billing Address:
    IPrimitive<Boolean> sameAsCurrent();

    @EmbeddedEntity
    AddressStructured billingAddress();

    @EmbeddedEntity
    Phone phone();
}
