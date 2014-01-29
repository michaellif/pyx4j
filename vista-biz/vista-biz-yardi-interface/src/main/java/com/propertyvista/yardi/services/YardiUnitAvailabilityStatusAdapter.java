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

import java.util.GregorianCalendar;

import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.VacateDate;
import com.yardi.entity.mits.Uniteconstatusinfo;
import com.yardi.entity.mits.Unitleasestatusinfo;
import com.yardi.entity.mits.Unitoccpstatusinfo;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.property.asset.unit.AptUnit;

/** Converts Yardi Unit Availability to Vista Unit Availability Status */
public class YardiUnitAvailabilityStatusAdapter {

    public UnitAvailabilityStatus extractAvailabilityStatus(ILSUnit unit) {
        UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);

        Unitoccpstatusinfo mitsOccupancyStatus = unit.getUnit().getInformation().get(0).getUnitOccupancyStatus();
        Unitleasestatusinfo mitsLeasedStatus = unit.getUnit().getInformation().get(0).getUnitLeasedStatus();
        Uniteconstatusinfo mitsEconomicStatus = unit.getUnit().getInformation().get(0).getUnitEcomomicStatus();

        if (mitsOccupancyStatus == Unitoccpstatusinfo.VACANT) {
            status.vacancyStatus().setValue(Vacancy.Vacant);
        } else if (mitsLeasedStatus == Unitleasestatusinfo.ON_NOTICE || mitsLeasedStatus == Unitleasestatusinfo.LEASED_RESERVED) {
            status.vacancyStatus().setValue(Vacancy.Notice);
        } else {
            status.vacancyStatus().setValue(null);
        }

        if (mitsLeasedStatus == Unitleasestatusinfo.AVAILABLE && mitsOccupancyStatus == Unitoccpstatusinfo.VACANT) {
            status.rentedStatus().setValue(RentedStatus.Unrented);
        } else if (mitsLeasedStatus == Unitleasestatusinfo.LEASED_RESERVED && mitsOccupancyStatus == Unitoccpstatusinfo.VACANT) {
            status.rentedStatus().setValue(RentedStatus.Rented);
        } else if (mitsOccupancyStatus == Unitoccpstatusinfo.OCCUPIED && mitsLeasedStatus == Unitleasestatusinfo.LEASED) {
            status.rentedStatus().setValue(null);
        } else if (mitsOccupancyStatus == Unitoccpstatusinfo.OCCUPIED && mitsLeasedStatus == Unitleasestatusinfo.ON_NOTICE) {
            status.rentedStatus().setValue(RentedStatus.Unrented);
        } else if (mitsOccupancyStatus == Unitoccpstatusinfo.OCCUPIED && mitsLeasedStatus == Unitleasestatusinfo.LEASED_RESERVED) {
            status.rentedStatus().setValue(RentedStatus.Rented);
        } else if (mitsOccupancyStatus == Unitoccpstatusinfo.VACANT && mitsLeasedStatus == Unitleasestatusinfo.OTHER) {
            if ((mitsEconomicStatus == Uniteconstatusinfo.DOWN || mitsEconomicStatus == Uniteconstatusinfo.MODEL)
                    && "Non Revenue Generating Unit".equals(unit.getUnit().getInformation().get(0).getUnitLeasedStatusDescription())) {
                status.rentedStatus().setValue(RentedStatus.OffMarket);
            }
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

        status.marketRent().setValue(unit.getUnit().getInformation().get(0).getMarketRent());

        if (unit.getAvailability() != null) {
            if (status.vacancyStatus().getValue() == Vacancy.Notice && status.rentedStatus().getValue() == RentedStatus.Unrented) {
                if (unit.getAvailability().getVacateDate() != null) {
                    VacateDate vacateDateIn = unit.getAvailability().getVacateDate();
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.clear();
                    cal.set(Integer.parseInt(vacateDateIn.getYear()), Integer.parseInt(vacateDateIn.getMonth()) - 1, Integer.parseInt(vacateDateIn.getDay()));
                    LogicalDate vacateDate = new LogicalDate(cal.getTime());
                    status.rentEndDay().setValue(vacateDate);
                }
            } else if (status.vacancyStatus().getValue() == Vacancy.Vacant && status.rentedStatus().getValue() == RentedStatus.Unrented) {
                if (unit.getAvailability().getVacateDate() != null) {
                    VacateDate vacateDateIn = unit.getAvailability().getVacateDate();
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.clear();
                    cal.set(Integer.parseInt(vacateDateIn.getYear()), Integer.parseInt(vacateDateIn.getMonth()) - 1, Integer.parseInt(vacateDateIn.getDay()));
                    LogicalDate vacateDate = new LogicalDate(cal.getTime());
                    status.vacantSince().setValue(vacateDate);
                }
            }
        }

        return status;

    }

    public void mergeUnitInfo(UnitAvailabilityStatus status, AptUnit unitId) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId.getPrimaryKey());
        Persistence.ensureRetrieve(unit.building(), AttachLevel.IdOnly);
        Persistence.ensureRetrieve(unit.floorplan(), AttachLevel.IdOnly);
        status.unit().set(unit);
        status.building().set(unit);
        status.floorplan().set(unit.floorplan());
    }

    /**
     * Attempt to fill lease related info of the availability status (like move-in, move out dates or actual rent) for the given status.
     * 
     * @param status
     *            must containt reference to partent unitunit reference
     */
    public void mergeLeaseInfo(UnitAvailabilityStatus status) {
        // TODO 
    }
}
