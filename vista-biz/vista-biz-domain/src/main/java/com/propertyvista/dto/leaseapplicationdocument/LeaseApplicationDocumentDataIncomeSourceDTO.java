/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2014
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.dto.leaseapplicationdocument;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface LeaseApplicationDocumentDataIncomeSourceDTO extends IEntity {

    IPrimitive<String> incomeSource();

    IPrimitive<String> employerName();

    IPrimitive<String> employmentDuration();

    IPrimitive<String> managerName();

    IPrimitive<String> managerPhone();

    // Employer Address

    IPrimitive<String> address1();

    IPrimitive<String> address2();

    IPrimitive<String> city();

    IPrimitive<String> province();

    IPrimitive<String> postalCode();

    IPrimitive<String> country();

    // Employement Info

    IPrimitive<BigDecimal> monthlySalary();

    IPrimitive<String> position();

    IPrimitive<LogicalDate> startDate();

    IPrimitive<LogicalDate> endDate();

}
