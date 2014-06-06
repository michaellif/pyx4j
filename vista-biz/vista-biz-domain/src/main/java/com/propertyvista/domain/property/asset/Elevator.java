/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import java.util.Date;

import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.building.BuildingMechanical;

public interface Elevator extends Equipment, BookingSchedule, BuildingMechanical {

    IPrimitive<Boolean> isForMoveInOut();

    @Timestamp
    IPrimitive<Date> updated();
}