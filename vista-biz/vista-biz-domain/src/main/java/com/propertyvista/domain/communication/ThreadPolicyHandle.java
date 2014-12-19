/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author michaellif
 */
package com.propertyvista.domain.communication;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@ToStringFormat("{0}")
@Table(prefix = "communication")
public interface ThreadPolicyHandle extends IEntity {

    @NotNull
    @Detached
    @MemberColumn(notNull = true)
    CommunicationEndpoint policyConsumer();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> hidden();

    @Owner
    @Detached
    @NotNull
    @MemberColumn(name = "thrd", notNull = true)
    @JoinColumn
    @Indexed
    @ToString(index = 0)
    CommunicationThread thread();
}
