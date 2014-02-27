/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.GeneratedValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.offering.ProductItem;

@ToStringFormat("{0,choice,null#{2}|!null#{0}}, agreed price: ${1}")
public interface BillableItem extends IEntity {

    @GeneratedValue(type = GeneratedValue.GenerationType.randomUUID)
    IPrimitive<String> uid();

    @ToString(index = 0)
    ProductItem item();

    @NotNull
    @ToString(index = 1)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> agreedPrice();

    @Caption(name = "Last Updated")
    @Timestamp
    IPrimitive<LogicalDate> updated();

    @Owned
    IList<BillableItemAdjustment> adjustments();

    @Owned
    IList<Deposit> deposits();

    @Owned
    BillableItemExtraData extraData();

    @Caption(description = "Empty value assumes Lease start date")
    IPrimitive<LogicalDate> effectiveDate();

    @Caption(description = "Empty value assumes Lease end date")
    IPrimitive<LogicalDate> expirationDate();

    @ToString(index = 2)
    IPrimitive<String> description();

    IPrimitive<String> yardiChargeCode();

    IPrimitive<Boolean> finalized();
}
