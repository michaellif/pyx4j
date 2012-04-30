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
package com.propertyvista.domain.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

/**
 * 
 * Actual payment record. {@link com.propertyvista.domain.financial.billing.InvoicePayment} captures payment portion for particular charge (in future version)
 * Deposit is considered as payment and presented by this class (this statement requires
 * review, Alex S, see deposit properties in
 * com.propertyvista.domain.financial.payment.java)
 * 
 * @author Alexs
 */
public interface PaymentRecord extends IEntity {

    @I18n
    enum Type {

        Cash,

        @Translate("Cheque")
        Check,

        Other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @I18n
    enum PaymentStatus {

        Received,

        Posted,

        Rejected,

        Canceled;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @Override
    @Indexed
    @ToString
    IPrimitive<Key> id();

    @Owner
    @ReadOnly
    @Detached
    @JoinColumn
    BillingAccount billingAccount();

    IPrimitive<LogicalDate> receivedDate();

    /**
     * Do not post before that date
     */
    IPrimitive<LogicalDate> targetDate();

    IPrimitive<LogicalDate> depositDate();

    @NotNull
    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @NotNull
    @MemberColumn(name = "paymentType")
    IPrimitive<Type> type();

    IPrimitive<PaymentStatus> paymentStatus();

    // internals:
    interface OrderId extends ColumnId {
    }

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();
}
