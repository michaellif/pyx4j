/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.maintenance.MaintenanceRequest;

@Transient
@ExtendsBO
public interface MaintenanceRequestDTO extends MaintenanceRequest {

    @Caption(name = "Problem in my Apartment")
    IPrimitive<Boolean> reportedForOwnUnit();

    IPrimitive<LogicalDate> scheduledDate();

    @Editor(type = EditorType.timepicker)
    @Format("h:mm a")
    IPrimitive<Time> scheduledTimeFrom();

    @Editor(type = EditorType.timepicker)
    @Format("h:mm a")
    IPrimitive<Time> scheduledTimeTo();

}
