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
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.security.SecurityQuestion;

@Transient
public interface AccountRecoveryOptionsDTO extends IEntity {

    IList<SecurityQuestion> securityQuestions();

    @Caption(name = "Password Recovery email address")
    IPrimitive<String> recoveryEmail();

    IPrimitive<String> mobilePhone();

    IPrimitive<String> securityQuestion();

    IPrimitive<String> securityAnswer();

}
