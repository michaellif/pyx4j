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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

public interface IPerson {

    @Caption(name = "First Name")
    @ToString(index = 1)
    @NotNull
    public abstract IPrimitive<String> firstName();

    @Caption(name = "Middle")
    public abstract IPrimitive<String> middleName();

    @Caption(name = "Last Name")
    @ToString(index = 2)
    @NotNull
    public abstract IPrimitive<String> lastName();

    @Caption(name = "Birth Date")
    @NotNull
    public abstract IPrimitive<Date> birthDate();

    @Editor(type = EditorType.phone)
    @Caption(name = "Home")
    public abstract IPrimitive<String> homePhone();

    @Editor(type = EditorType.phone)
    @Caption(name = "Mobile")
    public abstract IPrimitive<String> mobilePhone();

    @Editor(type = EditorType.phone)
    @Caption(name = "Work")
    public abstract IPrimitive<String> workPhone();

    @Editor(type = EditorType.email)
    @Caption(name = "Email")
    public abstract IPrimitive<String> email();

}