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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.lease.Tenant;

@ToStringFormat("{0}, {1,choice,null#|!null#{1}}{2,choice,null#|!null#{2}} - {3}")
public interface PreauthorizedPayment extends IEntity {

    @I18n
    public enum AmountType {

        Percent, Value;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    @Caption(name = "Type")
    IPrimitive<AmountType> amountType();

    @NotNull
    @ReadOnly
    @ToString(index = 1)
    @Format("#,##0.00")
    @Caption(name = "Amount")
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> percent();

    @NotNull
    @ReadOnly
    @ToString(index = 2)
    @Format("#,##0.00")
    @Caption(name = "Amount")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> value();

    @NotNull
    IPrimitive<Boolean> isDeleted();

    @NotNull
    @ReadOnly
    @ToString(index = 3)
    LeasePaymentMethod paymentMethod();

    @Length(40)
    IPrimitive<String> comments();

    // internals: -------------------------------------------------------------

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
