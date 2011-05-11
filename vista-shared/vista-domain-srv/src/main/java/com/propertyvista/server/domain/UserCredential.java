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
package com.propertyvista.server.domain;

import java.util.Date;

import com.propertyvista.common.domain.User;
import com.propertyvista.common.domain.VistaBehavior;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@RpcBlacklist
@Table(primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED, expands = User.class)
public interface UserCredential extends IEntity {

    IPrimitive<Boolean> enabled();

    @RpcTransient
    IPrimitive<String> credential();

    @Detached
    @MemberColumn(name = "usr")
    User user();

    IPrimitive<VistaBehavior> behavior();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @RpcTransient
    IPrimitive<String> accessKey();

    @RpcTransient
    IPrimitive<Date> accessKeyExpire();

}
