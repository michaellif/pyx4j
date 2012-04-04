/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.person.Person.Sex;

public interface _CustomerPerson extends _Customer {

    @ToString(index = 1)
    @BusinessEqualValue
    @EmbeddedEntity
    Name name();

    @NotNull
    IPrimitive<Sex> sex();

    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> birthDate();
}
