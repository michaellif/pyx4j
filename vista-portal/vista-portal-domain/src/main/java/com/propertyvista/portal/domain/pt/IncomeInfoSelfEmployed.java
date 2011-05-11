/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.propertyvista.common.domain.Money;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface IncomeInfoSelfEmployed extends IEntity, IEmploymentInfo {

    @Override
    @Caption(name = "Name of Company")
    IPrimitive<String> name();

    @Override
    @Caption(name = "Years in business")
    IPrimitive<Double> employedForYears();

    @Caption(name = "Is fully owned")
    IPrimitive<Boolean> fullyOwned();

    @Caption(name = "Monthly revenue")
    Money monthlyRevenue();

    @Override
    @Caption(name = "Monthly salary/dividend")
    @NotNull
    Money monthlyAmount();

    @Caption(name = "Number of employees")
    IPrimitive<Integer> numberOfEmployees();
}
