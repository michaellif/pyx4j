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

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IPrimitive;

public interface IEmploymentInfo {

    @Caption(name = "Supervisor/Manager Name")
    public abstract IPrimitive<String> supervisorName();

    @Caption(name = "Supervisor/Manager Phone")
    public abstract IPrimitive<String> supervisorPhone();

    @Caption(name = "Monthly salary")
    public abstract IPrimitive<Double> monthlySalary();

    @Caption(name = "Position")
    public abstract IPrimitive<String> position();

    //TODO: either one of below may be optional/hidden
    @Caption(name = "Employed for (years)")
    IPrimitive<Integer> employedForYears();

    @Caption(name = "Started on")
    IPrimitive<Date> jobStart();

    @Caption(name = "Ended on")
    IPrimitive<Date> jobEnd();
}