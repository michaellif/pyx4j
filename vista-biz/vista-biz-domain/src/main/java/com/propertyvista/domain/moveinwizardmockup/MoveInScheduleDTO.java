/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.moveinwizardmockup;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface MoveInScheduleDTO extends IEntity {

    IPrimitive<LogicalDate> moveInDay();

    IPrimitive<Boolean> reserveElevator();

    /** Only for UI */
    @Caption(name = "From")
    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> elevatorReserveFrom();

    /** Only for UI */
    @Caption(name = "To")
    @Editor(type = EditorType.timepicker)
    IPrimitive<Time> elevatorReserveTo();

    ScheduleDTO elevatorSchedule();
}
