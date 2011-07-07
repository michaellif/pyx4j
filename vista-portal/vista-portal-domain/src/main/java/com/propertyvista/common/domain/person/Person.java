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
package com.propertyvista.common.domain.person;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Person extends IEntity {

    @ToString
    @BusinessEqualValue
    @EmbeddedEntity
    Name name();

    @Editor(type = EditorType.phone)
    IPrimitive<String> homePhone();

    @Editor(type = EditorType.phone)
    IPrimitive<String> mobilePhone();

    @Editor(type = EditorType.phone)
    IPrimitive<String> workPhone();

    @Editor(type = EditorType.email)
    @Caption(name = "Email Address")
    IPrimitive<String> email();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> birthDate();
}