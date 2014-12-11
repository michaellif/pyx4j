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

import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.contact.InternationalAddress;

@AbstractEntity
@ToStringFormat("{0}{1,choice,null#|!null# - {1}}")
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
    InternationalAddress billingAddress();

    /**
     * Indicates if this method is deleted (still persists in DB but not used anymore!).
     */
    @NotNull
    IPrimitive<Boolean> isDeleted();

    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Format("yyyy-MM-dd HH:mm:ss")
    @Editor(type = EditorType.label)
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

}
