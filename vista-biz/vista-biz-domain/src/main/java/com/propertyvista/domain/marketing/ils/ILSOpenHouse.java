/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.marketing.ils;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.marketing.Marketing;

public interface ILSOpenHouse extends IEntity {
    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    Marketing marketing();

    interface OpenHouseDateId extends ColumnId {
        // will sort by date
    }

    @MemberColumn(OpenHouseDateId.class)
    IPrimitive<LogicalDate> eventDate();

    @Editor(type = Editor.EditorType.timepicker)
    IPrimitive<Time> startTime();

    @Editor(type = Editor.EditorType.timepicker)
    IPrimitive<Time> endTime();

    @Length(1000)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> details();

    IPrimitive<Boolean> appointmentRequired();

}
