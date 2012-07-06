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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
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

import com.propertyvista.domain.financial.BillingAccount;

public interface Deposit extends IEntity {

    /*
     * Policy type defines various aspects of policy processing including
     * - target products
     * - additional coverage rules
     * - refund rules
     */
    @I18n
    @XmlType(name = "DepositType")
    public enum DepositType {
        /*
         * Move-In Deposit will is used as a guarantee of move-in intent and to cover any move-in damages.
         * The remainder is used towards the first payment. Immediate Credit is issued on the First Bill.
         */
        MoveInDeposit,
        /*
         * Security Deposit can be used to cover various damages; otherwise will cover the
         * associated product fee and the reminder will be refunded according to the applicable policy.
         * Immediate Credit is issued 2 weeks (configurable) after the product expiration date.
         */
        SecurityDeposit,
        /*
         * Last Month Deposit can be used only to cover the last month payment and must be refunded in full
         * at the end of lease.
         * Immediate Credit is issued on the Last Bill.
         */
        LastMonthDeposit;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "DepositStatus")
    public enum DepositStatus {
        Created, Billed, Returned;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    @XmlType(name = "ValueType")
    public enum ValueType {
        Amount, Percentage;

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
    @JoinColumn
    BillingAccount billingAccount();

    @Detached
    BillableItem billableItem();

    @NotNull
    IPrimitive<DepositStatus> status();

    IPrimitive<LogicalDate> depositDate();

    IPrimitive<LogicalDate> refundDate();

    @NotNull
    @Length(40)
    @ToString(index = 2)
    @Editor(type = EditorType.textarea)
    IPrimitive<String> description();

    @NotNull
    @ToString(index = 3)
    IPrimitive<DepositType> type();

    @NotNull
    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> initialAmount();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> currentAmount();

    @Owned
    @Detached
    IList<DepositInterestAdjustment> interestAdjustments();

    // internals:
    interface OrderId extends ColumnId {
    }

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();
}
