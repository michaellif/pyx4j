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
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Tenant;

@ToStringFormat("{0}, {1,choice,null#|!null#{1}}{2,choice,null#|!null#{2}} - {3}")
public interface PreauthorizedPayment extends IEntity {

    public interface PreauthorizedPaymentCoveredItem extends IEntity {

        @NotNull
        @ReadOnly
        @ToString(index = 0)
        BillableItem billableItem();

        @NotNull
        @ReadOnly
        @ToString(index = 1)
        @Format("#,##0.00")
        @Editor(type = EditorType.money)
        IPrimitive<BigDecimal> amount();

        // -----------------------------

        @Owner
        @ReadOnly
        @Detached
        @NotNull
        @JoinColumn
        @MemberColumn(notNull = true)
        PreauthorizedPayment pap();

        @OrderColumn
        IPrimitive<Integer> orderId();
    }

    @Owned
    IList<PreauthorizedPaymentCoveredItem> coveredItems();

    @Deprecated
    @I18n
    public enum AmountType {

        Percent, Value;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Deprecated
    @NotNull
    @ReadOnly
    @ToString(index = 0)
    @Caption(name = "Type")
    IPrimitive<AmountType> amountType();

    @Deprecated
    @NotNull
    @ReadOnly
    @ToString(index = 1)
    @Format("#,##0.00")
    @Editor(type = EditorType.percentage)
    @MemberColumn(precision = 5, scale = 4)
    IPrimitive<BigDecimal> percent();

    @Deprecated
    @NotNull
    @ReadOnly
    @ToString(index = 2)
    @Format("#,##0.00")
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

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<LogicalDate> creationDate();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();
}
