/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.security.SecurityQuestion;

@Transient
public interface AccountRecoveryOptionsDTO extends IEntity {

    /** this is additional authentication info, the server must use this field to perform additional check */
    IPrimitive<String> password();

    /** the list of suggestions that appear on the form when user wants to change a security question, filled by the server when user retrieves its old data */
    IList<SecurityQuestion> securityQuestionsSuggestions();

    @Editor(type = EditorType.email)
    @Caption(description = "The system will this address as destination for recovery emails")
    IPrimitive<String> recoveryEmail();

    IPrimitive<String> mobilePhone();

    /** when is <code>true</code>: {@link #securityQuestion() and #securityAnswer() become mandatory} */
    IPrimitive<Boolean> useSecurityQuestionChallengeForPasswordReset();

    @NotNull
    IPrimitive<String> securityQuestion();

    @NotNull
    IPrimitive<String> securityAnswer();

}
