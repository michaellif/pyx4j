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
package com.propertyvista.domain.tenant.income;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.Money;

@DiscriminatorValue("selfEmployed")
@Caption(name = "Income Information Self Employed")
public interface IncomeInfoSelfEmployed extends IEmploymentInfo {

    @Override
    @Caption(name = "Name Of Company")
    IPrimitive<String> name();

    @Override
    @Caption(name = "Years In Business")
    IPrimitive<Double> employedForYears();

    @Caption(name = "Is Fully Owned")
    IPrimitive<Boolean> fullyOwned();

    @Caption(name = "Monthly Revenue")
    Money monthlyRevenue();

    @Override
    @Caption(name = "Monthly Salary/Dividend")
    @NotNull
    Money monthlyAmount();

    @Caption(name = "Number Of Employees")
    IPrimitive<Integer> numberOfEmployees();
}
