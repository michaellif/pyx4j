/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.reservation;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;

public interface ReservationSchedule extends IEntity {

    Building building();

    Tenant tenant();

    IPrimitive<String> comment();

    @MemberColumn(name = "reservationDate")
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> date();

    @Editor(type = Editor.EditorType.timepicker)
    @ToString
    @NotNull
    @Format("h:mm a")
    @MemberColumn(name = "tm")
    IPrimitive<Time> time();
}
