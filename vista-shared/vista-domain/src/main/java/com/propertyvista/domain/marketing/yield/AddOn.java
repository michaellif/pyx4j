/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-22
 * @author aroytbur
 * @version $Id$
 */
package com.propertyvista.domain.marketing.yield;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@ToStringFormat("{0} {1}")
public interface AddOn extends IEntity {

    @ToString
    @MemberColumn(name = "addOnType")
    IPrimitive<String> type();

    IPrimitive<String> description();

    IPrimitive<String> term();

    @Format("#0.00")
    @MemberColumn(name = "addOnValue")
    IPrimitive<Double> value();
}
