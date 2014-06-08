/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-23
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.dto;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;

@Transient
@ExtendsBO
@ToStringFormat("{0} {1}")
public interface MessageDTO extends Message {

    @NotNull
    IList<CommunicationEndpointDTO> to();

    @Override
    @NotNull
    CommunicationEndpointDTO sender();

    @Detached
    CommunicationEndpointDTO owner();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> allowedReply();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isInRecipients();

    @NotNull
    IPrimitive<ThreadStatus> status();

    @NotNull
    @Detached
    @MemberColumn(notNull = true)
    MessageCategory topic();

    @NotNull
    @ReadOnly
    IPrimitive<Date> created();

    @NotNull
    @ReadOnly
    IPrimitive<String> subject();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isRead();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> star();

    @NotNull
    @Detached
    IList<MessageDTO> content();

}
