/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ISet;

@Caption(name = "Role")
public interface CrmRole extends IEntity {

    @ToString
    @Length(50)
    @NotNull
    @MemberColumn(notNull = true)
    IPrimitive<String> name();

    @Length(100)
    @Editor(type = EditorType.textarea)
    IPrimitive<String> description();

    @Caption(name = "Permissions")
    IPrimitiveSet<VistaCrmBehavior> behaviors();

    @Transient
    IList<VistaCrmBehaviorDTO> permissions();

    @MemberColumn(name = "rls")
    ISet<CrmRole> roles();

    /**
     * <b>Warning:</b> whenever this value is about to be used to implement some logic, if a role has {@link VistaCrmBehavior#Equifax} in {@link #behaviors()},
     * the value this member
     * holds should be IGNORED as treated as <code>true</code>.
     */
    @Caption(description = "Require additional authentication challenge when a user logs in to the CRM, for users with Equifax premission this option is mandatory and cannot be overriden.")
    IPrimitive<Boolean> requireTwoStepVerificationOnLogin();

    @Caption(description = "Require a security question / answer challenge when a user request to change his/her password.")
    IPrimitive<Boolean> requireSecurityQuestionForPasswordReset();

    @Timestamp
    IPrimitive<Date> updated();

}
