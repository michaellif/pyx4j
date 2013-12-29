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
package com.propertyvista.domain.financial.tax;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.framework.PolicyNode;

//TODO add IVersionedEntity which will have effective date, expiration date
@ToStringFormat("{0}, {1}, {2}")
public interface Tax extends IEntity {

    @NotNull
    @Length(25)
    @ToString(index = 0)
    IPrimitive<String> name();

    @Length(50)
    @ToString(index = 1)
    IPrimitive<String> authority();

    //TODO rate for particular period
    @NotNull
    @Format("#,##0.00")
    @ToString(index = 2)
    @MemberColumn(scale = 4)
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> rate();

    IPrimitive<Boolean> compound();

    PolicyNode policyNode();

    @Timestamp
    IPrimitive<Date> updated();
}