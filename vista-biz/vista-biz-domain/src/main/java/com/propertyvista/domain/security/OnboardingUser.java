/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.common.AbstractUser;

@Caption(name = "User")
@Table(namespace = VistaNamespace.operationsNamespace)
public interface OnboardingUser extends AbstractUser {

    Pmc pmc();

    IPrimitive<String> firstName();

    IPrimitive<String> lastName();

    @Override
    @Editor(type = EditorType.email)
    @NotNull
    @Length(64)
    @Indexed(ignoreCase = true)
    IPrimitive<String> email();

    @NotNull
    @Editor(type = EditorType.password)
    @LogTransient
    @MemberColumn(name = "credential")
    IPrimitive<String> password();

    @Override
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();
}
