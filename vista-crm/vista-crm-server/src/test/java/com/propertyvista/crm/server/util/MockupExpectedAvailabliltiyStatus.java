/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadinessStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.VacancyStatus;

public interface MockupExpectedAvailabliltiyStatus extends IEntity {

    IPrimitive<Integer> testCaseNumber();

    IPrimitive<LogicalDate> statusDate();

    IPrimitive<VacancyStatus> vacancyStatus();

    IPrimitive<RentReadinessStatus> rentReadinessStatus();

    IPrimitive<Boolean> isScoped();

    IPrimitive<RentedStatus> rentedStatus();

    IPrimitive<LogicalDate> moveOutDay();

    IPrimitive<LogicalDate> moveInDay();
}
