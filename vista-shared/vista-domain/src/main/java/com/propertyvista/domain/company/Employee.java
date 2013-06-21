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

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.CascadeType;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUser;

@ToStringFormat("{0}, {1}")
public interface Employee extends Person {

    @NotNull
    @ToString(index = 63)
    @Caption(name = "Id")
    @Length(14)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> employeeId();

    @ToString(index = 64)
    IPrimitive<String> title();

    @Length(250)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Override
    IPrimitive<LogicalDate> birthDate();

    @NotNull
    @ReadOnly
    @Detached
    @MemberColumn(name = "user_id")
    CrmUser user();

    @Timestamp
    IPrimitive<Date> updated();

    @Detached(level = AttachLevel.Detached)
    IList<Portfolio> portfolios();

    @JoinTable(value = EmployeeBuildingAccess.class, cascade = CascadeType.ALL)
    @OrderBy(PrimaryKey.class)
    @Detached(level = AttachLevel.Detached)
    IList<Building> buildingAccess();

    @Detached
    IList<Employee> employees();

    @Detached
    Employee manager();

    @Owned
    @OrderBy(PrimaryKey.class)
    @Detached(level = AttachLevel.Detached)
    IList<Notification> notifications();
}
