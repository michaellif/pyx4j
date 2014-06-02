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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.MessageAttachment;

@Transient
@ExtendsBO
@ToStringFormat("{0} {1}")
public interface CommunicationMessageDTO extends DeliveryHandle {

    @NotNull
    IPrimitive<String> subject();

    @NotNull
    IList<CommunicationEndpointDTO> to();

    @Override
    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isRead();

    @Detached
    @NotNull
    CommunicationThreadDTO threadDTO();

    @NotNull
    @Detached
    CommunicationEndpoint sender();

    @NotNull
    @Length(2048)
    @Editor(type = Editor.EditorType.textarea)
    @ToString(index = 1)
    IPrimitive<String> text();

    @MemberColumn(name = "messageDate")
    @ToString(index = 0)
    @Format("MM/dd/yyyy, HH:mm:ss")
    IPrimitive<Date> date();

    @NotNull
    @Editor(type = EditorType.combo)
    IPrimitive<Boolean> isHighImportance();

    @Owned
    @Detached
    @OrderBy(PrimaryKey.class)
    IList<MessageAttachment> attachments();
}
