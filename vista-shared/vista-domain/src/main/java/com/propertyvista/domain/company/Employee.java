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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.CrmUser;

@ToStringFormat("{0}, {1}")
public interface Employee extends Person {

    @ToString(index = 10)
    IPrimitive<String> title();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @NotNull
    @ReadOnly
    @Detached
    @MemberColumn(name = "user_id")
    CrmUser user();

    @Owned
    @Detached
    IList<AssignedPortfolio> portfolios();

    @Owned
    @Detached
    IList<ManagedEmployee> employees();

    @Owner
    @Detached
    Employee manager();
}
