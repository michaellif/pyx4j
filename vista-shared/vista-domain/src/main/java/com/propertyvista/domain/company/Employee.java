/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.company;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Person;

public interface Employee extends Person {

    @ToString(index = -1)
    IPrimitive<String> title();

    IPrimitive<String> description();

    @Owned
    IList<AssignedPortfolio> portfolios();

    @Owned
    IList<ManagedEmployee> employees();

    @Detached
    Employee manager(); // double link!..
}
