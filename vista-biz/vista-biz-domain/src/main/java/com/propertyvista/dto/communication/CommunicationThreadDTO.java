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
 * @author igors
 */
package com.propertyvista.dto.communication;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.NotificationDelivery.NotificationType;
import com.propertyvista.domain.company.Employee;

@Transient
@ExtendsBO
public interface CommunicationThreadDTO extends CommunicationThread {

    public enum ViewScope {
        DispatchQueue, Messages, MessageCategory, TicketCategory
    }

    @Transient
    IPrimitive<ViewScope> viewScope();

    @Override
    @Detached
    Employee owner();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isDirect();

    @ReadOnly
    IPrimitive<Boolean> hidden();

    @Length(48000)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> deliveredText();

    IPrimitive<LogicalDate> dateFrom();

    IPrimitive<LogicalDate> dateTo();

    IPrimitive<NotificationType> notificationType();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isRead();

    @ReadOnly
    IPrimitive<Boolean> hasAttachments();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> star();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> highImportance();

    @NotNull
    @Detached
    IList<MessageDTO> childMessages();

    @NotNull
    MessageDTO representingMessage();

    @ReadOnly
    IPrimitive<Date> date();

    @ReadOnly
    IPrimitive<String> senders();

    @ReadOnly
    IPrimitive<Integer> messagesInThread();
}
