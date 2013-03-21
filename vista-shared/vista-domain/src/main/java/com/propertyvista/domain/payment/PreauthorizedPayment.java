/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-13
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.lease.Tenant;

public interface PreauthorizedPayment extends IEntity {

    public enum AmountType {

        Percent, Value;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // TODO use @OrderBy(PrimaryKey.class) in Tenant
    @Override
    @Indexed
    @OrderColumn
    IPrimitive<Key> id();

    @NotNull
    @ToString(index = 0)
    @ReadOnly
    IPrimitive<AmountType> amountType();

    @NotNull
    @ToString(index = 1)
    @Format("#,##0.00")
    @ReadOnly
    IPrimitive<BigDecimal> amount();

    @NotNull
    IPrimitive<Boolean> isDeleted();

    @NotNull
    @ToString(index = 2)
    @ReadOnly
    LeasePaymentMethod paymentMethod();

    @Length(40)
    IPrimitive<String> comments();

    // internals:

    @Owner
    @ReadOnly
    @Detached
    @NotNull
    @JoinColumn
    @MemberColumn(notNull = true)
    Tenant tenant();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> creationDate();

}
