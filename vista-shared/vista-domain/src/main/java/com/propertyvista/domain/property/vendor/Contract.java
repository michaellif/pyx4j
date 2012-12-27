/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.vendor;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Contract extends IEntity {

    @ToString(index = 1)
    @Caption(name = "Contract Number")
    IPrimitive<String> contractID();

    @ToString(index = 2)
    Vendor contractor();

    @MemberColumn(name = "vendorCost")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> cost();

    @Caption(name = "Start Date")
    @MemberColumn(name = "contractStart")
    IPrimitive<LogicalDate> start();

    @Caption(name = "Expiry Date")
    @MemberColumn(name = "contractEnd")
    IPrimitive<LogicalDate> end();

}
