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
package com.propertyvista.domain.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.BillingAccount;

/**
 * 
 * Actual payment record. {@link com.propertyvista.domain.financial.billing.BillPayment}
 * captures payment portion for particular charge (in future version)
 * Deposit is considered as payment and presented by this class.
 * 
 */
public interface Payment extends IEntity {

    @ReadOnly
    IPrimitive<Integer> sequenceNumber();

    IPrimitive<LogicalDate> depositDate();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @JoinTable(value = BillPayment.class, cascade = false)
    BillPayment billPayment();

    @Owner
    @ReadOnly
    @Detached
    @JoinColumn
    BillingAccount billingAccount();

    // internals:
    interface OrderId extends ColumnId {
    }

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();
}
