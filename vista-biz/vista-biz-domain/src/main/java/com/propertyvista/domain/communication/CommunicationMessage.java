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
 * @version $Id$
 */
package com.propertyvista.domain.communication;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

@ToStringFormat("{0} {1}")
public interface CommunicationMessage extends IEntity {

    @NotNull
    @Detached
    CommunicationEndpoint sender();

    @NotNull
    @Detached
    IList<CommunicationEndpoint> to();

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

    @NotNull
    @ReadOnly
    IPrimitive<Boolean> isRead();

    @Owner
    @Detached
    @NotNull
    @MemberColumn(notNull = true)
    @JoinColumn
    CommunicationThread thread();

    @Owned
    @Detached
    @OrderBy(PrimaryKey.class)
    IList<CommunicationMessageAttachment> attachments();
}
