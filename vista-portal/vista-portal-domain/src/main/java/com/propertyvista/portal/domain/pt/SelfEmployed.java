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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain.pt;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface SelfEmployed extends IEntity, IAddress {
    @Caption(name = "Name of Company")
    IPrimitive<String> companyName();

    @Caption(name = "Years in business")
    IPrimitive<Integer> yearsInBusiness();

    @Caption(name = "Is fully owned")
    IPrimitive<Boolean> fullyOwned();

    @Caption(name = "Monthly revenue")
    Money monthlyRevenue();

    @Caption(name = "Monthly salary/dividend")
    Money monthlySalary();

    @Caption(name = "Number of employees")
    IPrimitive<Integer> numberOfEmployees();
}
