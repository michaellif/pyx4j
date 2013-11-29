/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.property.asset.BuildingElement;

@ToStringFormat("{0}, {1}, ${2}")
public interface ProductItem extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @JoinColumn
    @ToString(index = 0)
    @Detached(level = AttachLevel.ToStringMembers)
    @Indexed
    Product.ProductV<?> product();

    @OrderColumn
    IPrimitive<Integer> _orderColumn();

    @NotNull
    @ToString(index = 1)
    @Deprecated
    //TODO move to Product 
    ARCode code();

    @Length(50)
    @ToString(index = 1)
    IPrimitive<String> name();

    @NotNull
    @ToString(index = 2)
    @Format("#,##0.00")
    @Caption(name = "Market Price")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> price();

    @Length(250)
    IPrimitive<String> description();

    interface BuildingElementColumnId extends ColumnId {
    }

    @JoinColumn(BuildingElementColumnId.class)
    @Detached
    BuildingElement element();

    @Caption(name = "Default")
    @Deprecated
    //TODO move to Product 
    IPrimitive<Boolean> isDefault();
}
