/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.mits.Unitleasestatusinfo;
import com.yardi.entity.mits.Unitoccpstatusinfo;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;

/** Converts Yardi Unit Availability to Vista Unit Availability Status */
public class YardiUnitAvailabilityAdapter {

    public UnitAvailabilityStatus extractAvailabilityStatus(ILSUnit unit) {

        UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);

        Unitoccpstatusinfo occupancyStatus = unit.getUnit().getInformation().get(0).getUnitOccupancyStatus();
        if (occupancyStatus == Unitoccpstatusinfo.VACANT) {
            status.vacancyStatus().setValue(Vacancy.Vacant);
        }

        Unitleasestatusinfo leasedStatus = unit.getUnit().getInformation().get(0).getUnitLeasedStatus();
        if (leasedStatus == Unitleasestatusinfo.LEASED_ON_NOTICE || leasedStatus == Unitleasestatusinfo.ON_NOTICE) {
            status.vacancyStatus().setValue(Vacancy.Notice);
        }

        if (leasedStatus == Unitleasestatusinfo.LEASED_RESERVED || leasedStatus == Unitleasestatusinfo.RESERVED) {
            status.rentedStatus().setValue(RentedStatus.Rented);
        } else {
            status.rentedStatus().setValue(RentedStatus.Unrented);
        }
        if (occupancyStatus == Unitoccpstatusinfo.OCCUPIED && leasedStatus == Unitleasestatusinfo.OTHER) {
            status.rentedStatus().setValue(RentedStatus.OffMarket);
        }

        if (unit.getComment() != null && unit.getComment() instanceof String) {
            String comment = (String) unit.getComment();
            if (comment.equals("RentReady=true")) {
                status.rentReadinessStatus().setValue(RentReadiness.RentReady);
            }
            if (comment.equals("RentReady=false")) {
                status.rentReadinessStatus().setValue(null);
            }
        }
        return status;
    }
}
