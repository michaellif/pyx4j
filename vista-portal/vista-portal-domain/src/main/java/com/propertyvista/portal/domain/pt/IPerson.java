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

import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

public interface IPerson {

    @Caption(name = "First Name")
    @ToString(index = 1)
    @NotNull
    @BusinessEqualValue
    IPrimitive<String> firstName();

    @Caption(name = "Middle")
    IPrimitive<String> middleName();

    @Caption(name = "Last Name")
    @ToString(index = 2)
    @NotNull
    @BusinessEqualValue
    IPrimitive<String> lastName();

    @Editor(type = EditorType.phone)
    @Caption(name = "Home Phone")
    IPrimitive<String> homePhone();

    @Editor(type = EditorType.phone)
    @Caption(name = "Mobile Phone")
    IPrimitive<String> mobilePhone();

    @Editor(type = EditorType.phone)
    @Caption(name = "Work Phone")
    IPrimitive<String> workPhone();

    @Editor(type = EditorType.email)
    @Caption(name = "Email Address")
    IPrimitive<String> email();

}