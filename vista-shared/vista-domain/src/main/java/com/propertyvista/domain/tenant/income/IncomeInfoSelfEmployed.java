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

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

@DiscriminatorValue("selfEmployed")
@Caption(name = "Income Information Self Employed")
public interface IncomeInfoSelfEmployed extends IEmploymentInfo {

    @NotNull
    @Override
    @ToString(index = 1)
    @Caption(name = "Name Of Company")
    IPrimitive<String> name();

    @Override
    @Caption(name = "Years In Business")
    IPrimitive<Double> employedForYears();

    @Override
    @Caption(name = "Supervisor/Manager Name")
    IPrimitive<String> supervisorName();

    @Override
    @Editor(type = EditorType.phone)
    @Caption(name = "Supervisor/Manager Phone")
    IPrimitive<String> supervisorPhone();

    @Caption(name = "Is Fully Owned")
    IPrimitive<Boolean> fullyOwned();

    @Caption(name = "Monthly Revenue")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> monthlyRevenue();

    @NotNull
    @Override
    @ToString(index = 0)
    @Caption(name = "Monthly Salary/Dividend")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> monthlyAmount();

    @Caption(name = "Number Of Employees")
    IPrimitive<Integer> numberOfEmployees();
}
