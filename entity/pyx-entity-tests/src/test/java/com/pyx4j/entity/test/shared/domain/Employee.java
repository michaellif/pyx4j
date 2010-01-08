/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Employee extends IEntity<Employee> {

    public static enum EmploymentStatus {
        DISMISSED, FULL_TIME, PART_TIME, CONTRACT
    }

    IPrimitive<String> firstName();

    IPrimitive<Date> hiredate();

    IPrimitive<Boolean> reliable();

    IPrimitive<Integer> rating();

    IPrimitive<Double> salary();

    IPrimitive<EmploymentStatus> employmentStatus();

    IPrimitive<Status> accessStatus();

    Department department();

    Employee manager();

    @Owned
    @NotNull
    Address homeAddress();

    @Owned
    Address workAddress();

}
