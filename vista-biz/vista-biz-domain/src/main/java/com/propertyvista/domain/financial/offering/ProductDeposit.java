/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IMoneyPercentAmount;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.tenant.lease.Deposit;

@EmbeddedEntity
@ToStringFormat("{2}, {1}, {3}")
public interface ProductDeposit extends IEntity {

    @NotNull
    @Editor(type = EditorType.radiogroup)
    IPrimitive<Boolean> enabled();

    /** passed to Deposit to use for corresponding invoice line items */
    @NotNull
    ARCode chargeCode();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "depositValue")
    IMoneyPercentAmount value();

    @NotNull
    @ToString(index = 1)
    IPrimitive<ValueType> valueType();

    @NotNull
    @Length(40)
    @ToString(index = 2)
    IPrimitive<String> description();

    @NotNull
    @ToString(index = 3)
    IPrimitive<Deposit.DepositType> depositType();
}
