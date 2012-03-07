/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@Inheritance
@AbstractEntity
public interface BillEntry extends IEntity {

    enum Period {
        previous, current, next
    }

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @Owned
    IList<BillChargeTax> taxes();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> taxTotal();

    IPrimitive<Period> period();

    interface OrderId extends ColumnId {

    }

    @Owner
    @Detached
    @NotNull
    @Indexed
    @JoinColumn
    Bill bill();

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderId();

}