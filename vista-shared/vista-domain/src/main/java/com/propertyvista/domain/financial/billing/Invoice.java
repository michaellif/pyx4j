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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@AbstractEntity
public interface Invoice extends IEntity {

    @Detached
    @OrderBy(InvoiceLineItem.OrderId.class)
    IList<InvoiceLineItem> lineItems();

    /**
     * The total amount due from the previous bill.
     */
    IPrimitive<BigDecimal> previousBalanceAmount();

    /**
     * The total amount of payments received since the previous bill, up to the current
     * Bill day.
     */
    IPrimitive<BigDecimal> paymentReceivedAmount();

    IPrimitive<BigDecimal> depositRefundAmount();

    IPrimitive<BigDecimal> immediateAdjustments();

    /**
     * pastDueAmount = previousBalanceAmount - paymentReceivedAmount - depositRefundAmount -
     * immediateAdjustments
     */
    IPrimitive<BigDecimal> pastDueAmount();

    IPrimitive<BigDecimal> serviceCharge();

    IPrimitive<BigDecimal> recurringFeatureCharges();

    IPrimitive<BigDecimal> oneTimeFeatureCharges();

    /**
     * 
     * It includes all feature/service adjustments as well as lease adjustments
     * 
     */
    IPrimitive<BigDecimal> totalAdjustments();

    IPrimitive<BigDecimal> depositAmount();

    IPrimitive<BigDecimal> credits();

    /**
     * currentAmount = pastDueAmount + serviceCharge + recurringFeatureCharges +
     * oneTimeFeatureCharges + totalAdjustments - depositPaidAmount
     */
    IPrimitive<BigDecimal> currentAmount();

    IPrimitive<BigDecimal> taxes();

    /**
     * totalDueAmount = currentAmount + taxes
     */
    IPrimitive<BigDecimal> totalDueAmount();

}
