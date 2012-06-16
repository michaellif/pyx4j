/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public interface Deposit extends IEntity {

    /*
     * Policy type defines various aspects of policy processing including
     * - target products
     * - additional coverage rules
     * - refund rules
     */
    public enum DepositType {
        /*
         * Security Deposit can be used to cover various damages; otherwise will cover the
         * associated product fee and the reminder will be refunded according to the applicable policy
         */
        SecurityDeposit,
        /*
         * Last Month Deposit can be used only to cover the last month payment and must be refunded in full
         * at the end of lease.
         */
        LastMonthDeposit,
        /*
         * Move-In Deposit will is used to cover any move-in damages and the reminder is used
         * towards the first payment
         */
        MoveInDeposit
    }

    @I18n
    @XmlType(name = "ValueType")
    public enum ValueType {
        amount,

        percentage;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "RepaymentMode")
    public enum RepaymentMode {

        applyToFirstMonth, applyToLastMonth, returnAtLeaseEnd;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Override
    @Indexed
    @ToString
    IPrimitive<Key> id();

    @Owner
    @ReadOnly
    @Detached
    //@JoinColumn
    BillableItem billableItem();

    IPrimitive<LogicalDate> depositDate();

    IPrimitive<LogicalDate> refundDate();

    @NotNull
    @ToString(index = 1)
    IPrimitive<ValueType> valueType();

    @NotNull
    @Length(20)
    @ToString(index = 2)
    IPrimitive<String> description();

    @NotNull
    @ToString(index = 3)
    IPrimitive<RepaymentMode> repaymentMode();

    IPrimitive<BigDecimal> initialAmount();

    IPrimitive<BigDecimal> currentAmount();

    @Owned
    IList<DepositInterestAdjustment> interestAdjustments();

    // internals:
    interface OrderId extends ColumnId {
    }

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();
}
