/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface UserCredentialEditDTO extends IEntity {

    @Caption(name = "Active")
    IPrimitive<Boolean> enabled();

    /* password is used for new entity creation only */
    @NotNull
    @Editor(type = EditorType.password)
    @Caption(name = "Password")
    @LogTransient
    IPrimitive<String> password();

    @NotNull
    @Editor(type = EditorType.password)
    @Caption(name = "Confirm password")
    @LogTransient
    IPrimitive<String> passwordConfirm();

    @Caption(name = "Require to change password on next sign in")
    IPrimitive<Boolean> requiredPasswordChangeOnNextLogIn();

    @ReadOnly
    @Editor(type = Editor.EditorType.label)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> passwordUpdated();

    @ReadOnly
    @Editor(type = Editor.EditorType.label)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> credentialUpdated();

    @ReadOnly
    IPrimitive<Date> crmCredentialUpdated();
}
