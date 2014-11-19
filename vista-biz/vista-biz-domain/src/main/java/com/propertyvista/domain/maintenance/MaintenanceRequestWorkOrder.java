/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.TimeWindow;

public interface MaintenanceRequestWorkOrder extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    MaintenanceRequest request();

    @Timestamp(Update.Created)
    IPrimitive<Date> created();

    @Timestamp(Update.Updated)
    @Caption(name = "Last Updated")
    IPrimitive<Date> updated();

    @NotNull
    IPrimitive<LogicalDate> scheduledDate();

    @EmbeddedEntity
    TimeWindow scheduledTime();

    @NotNull
    @Editor(type = EditorType.textarea)
    IPrimitive<String> workDescription();

    IPrimitive<String> progressNote();

    IPrimitive<Boolean> isEmergencyWork();

    @EmbeddedEntity
    @ReadOnly
    NoticeOfEntry noticeOfEntry();
}
