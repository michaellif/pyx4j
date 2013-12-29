/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security.common;

import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;


/**
 * Should be nice to move this to Server side domain package
 */
@AbstractEntity
@RpcBlacklist
@GwtBlacklist
public interface AbstractUserCredential<E extends AbstractUser> extends IEntity {

    @Detached
    @MemberColumn(name = "usr")
    @ReadOnly
    E user();

    IPrimitive<Boolean> enabled();

    IPrimitive<Boolean> requiredPasswordChangeOnNextLogIn();

    @RpcTransient
    IPrimitive<String> credential();

    IPrimitive<Date> passwordUpdated();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> credentialUpdated();

    @RpcTransient
    IPrimitive<String> accessKey();

    @RpcTransient
    IPrimitive<Date> accessKeyExpire();

    @Caption(name = "Password Recovery email address")
    IPrimitive<String> recoveryEmail();

    IPrimitive<String> securityQuestion();

    IPrimitive<String> securityAnswer();

}
