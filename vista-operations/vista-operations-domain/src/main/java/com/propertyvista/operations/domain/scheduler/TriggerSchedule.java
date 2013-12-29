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
package com.propertyvista.operations.domain.scheduler;

import java.util.Date;

import javax.xml.bind.annotation.XmlSchemaType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "scheduler", namespace = VistaNamespace.operationsNamespace)
@ToStringFormat("{0} {1}  Since {2}; Next Fire Time {3}")
public interface TriggerSchedule extends IEntity {

    @Owner
    @JoinColumn
    @ReadOnly
    @Detached
    @MemberColumn(name = "trgr", notNull = true)
    Trigger trigger();

    @OrderColumn
    IPrimitive<Integer> odr();

    @ToString(index = 0)
    @NotNull
    IPrimitive<ScheduleType> repeatType();

    @NotNull
    IPrimitive<Integer> repeatEvery();

    @Editor(type = Editor.EditorType.timepicker)
    @ToString(index = 1)
    @NotNull
    @Format("HH:mm")
    @Caption(watermark = "__:__")
    @MemberColumn(name = "tm")
    @XmlSchemaType(name = "time")
    IPrimitive<java.sql.Time> time();

    @Caption(name = "Starts on")
    @ToString(index = 2)
    @NotNull
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> startsOn();

    @Caption(name = "Ends on")
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> endsOn();

    @ToString(index = 3)
    @Transient
    @Editor(type = Editor.EditorType.label)
    @ReadOnly
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> nextFireTime();

}
