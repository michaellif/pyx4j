/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.domain;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface YardiLeaseCharge extends IEntity {

    IPrimitive<String> chargeId();

    IPrimitive<BigDecimal> amount();

    IPrimitive<LogicalDate> serviceToDate();

    IPrimitive<LogicalDate> serviceFromDate();

    IPrimitive<String> chargeCode();

    IPrimitive<String> glAccountNumber();

    IPrimitive<String> description();

    IPrimitive<String> comment();
}
