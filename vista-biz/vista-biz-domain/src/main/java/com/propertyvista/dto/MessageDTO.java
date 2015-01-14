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
package com.propertyvista.dto;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.communication.CommunicationAssociation;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.NotificationDelivery.NotificationType;
import com.propertyvista.domain.communication.SpecialDelivery.DeliveryMethod;
import com.propertyvista.domain.company.Employee;

@Transient
@ExtendsBO
public interface MessageDTO extends Message {

    public enum ViewScope {
        DispatchQueue, Messages, MessageCategory, TicketCategory
    }

    @Transient
    IPrimitive<ViewScope> viewScope();

    @NotNull
    IList<CommunicationEndpointDTO> to();

    @NotNull
    CommunicationEndpointDTO senderDTO();

    @Override
    @NotNull
    @Length(48000)
    @Editor(type = Editor.EditorType.richtextarea)
    IPrimitive<String> text();

    @Detached
    CommunicationEndpointDTO owner();

    @Detached
    Employee ownerForList();

    @ReadOnly
    IPrimitive<Boolean> allowedReply();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isInRecipients();

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isDirect();

    @NotNull
    IPrimitive<ThreadStatus> status();

    @ReadOnly
    IPrimitive<Boolean> hidden();

    @NotNull
    @Detached
    @MemberColumn(notNull = true)
    MessageCategory category();

    @NotNull
    @ReadOnly
    @Length(78)
    IPrimitive<String> subject();

    @ReadOnly
    IPrimitive<DeliveryMethod> deliveryMethod();

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
    @Detached
    IList<MessageDTO> content();

    @NotNull
    @ReadOnly
    @Editor(type = Editor.EditorType.label)
    MessageHeader header();

    @Detached
    CommunicationAssociation associated();

    @Transient
    @ToStringFormat("{0}; {1} {2,choice,null#|!null# on behalf of {2}} {3,choice,null#|!null#{3}}")
    public interface MessageHeader extends IEntity {
        @NotNull
        @ToString(index = 1)
        @ReadOnly
        IPrimitive<String> sender();

        @MemberColumn(name = "messageDate")
        @ToString(index = 0)
        @Format("MM/dd/yyyy, HH:mm:ss")
        @ReadOnly
        IPrimitive<Date> date();

        @ToString(index = 2)
        @ReadOnly
        IPrimitive<String> onBehalf();

        @ToString(index = 3)
        @ReadOnly
        IPrimitive<String> onBehalfVisible();
    }
}
