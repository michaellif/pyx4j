/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.proc;

import java.util.Date;

import javax.xml.bind.annotation.XmlSchemaType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@EmbeddedEntity
public interface ProcessSchedule extends IEntity {

    @ToString
    @NotNull
    IPrimitive<ScheduleType> repeatType();

    IPrimitive<Integer> repeatEvery();

    @Editor(type = Editor.EditorType.timepicker)
    @ToString
    @NotNull
    @Format("h:mma")
    @MemberColumn(name = "tm")
    @XmlSchemaType(name = "time")
    IPrimitive<java.sql.Time> time();

    @Caption(name = "Starts on")
    @ToString
    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> startsOn();

    @Caption(name = "Ends on")
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> endsOn();

    @Transient
    @Editor(type = Editor.EditorType.label)
    @ReadOnly
    @Format("MM/dd/yyyy h:mm a")
    IPrimitive<Date> nextFireTime();

}
