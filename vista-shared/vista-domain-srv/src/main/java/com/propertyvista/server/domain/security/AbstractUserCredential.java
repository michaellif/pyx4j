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
package com.propertyvista.server.domain.security;

import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.security.AbstractUser;

@AbstractEntity
@RpcBlacklist
public interface AbstractUserCredential<E extends AbstractUser> extends IEntity {

    @Detached
    @MemberColumn(name = "usr")
    @ReadOnly
    E user();

    IPrimitive<Boolean> enabled();

    IPrimitive<Boolean> requiredPasswordChangeOnNextLogIn();

    @RpcTransient
    IPrimitive<String> credential();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @RpcTransient
    IPrimitive<String> accessKey();

    @RpcTransient
    IPrimitive<Date> accessKeyExpire();

}
