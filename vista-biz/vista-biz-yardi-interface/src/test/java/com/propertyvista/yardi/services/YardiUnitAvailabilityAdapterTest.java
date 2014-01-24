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

import junit.framework.Assert;

import org.junit.Test;

import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.mits.Unitleasestatusinfo;
import com.yardi.entity.mits.Unitoccpstatusinfo;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;

public class YardiUnitAvailabilityAdapterTest {

    @Test
    public void testVacancyStatus() {
        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.VACANT);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(Vacancy.Vacant, status.vacancyStatus().getValue());
        }

        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.LEASED_ON_NOTICE);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(Vacancy.Notice, status.vacancyStatus().getValue());
        }

        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.ON_NOTICE);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(Vacancy.Notice, status.vacancyStatus().getValue());
        }
    }

    @Test
    public void testRentedStatus() {
        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.LEASED);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(null, status.vacancyStatus().getValue());
        }

        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.LEASED_ON_NOTICE);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(RentedStatus.Unrented, status.rentedStatus().getValue());
        }
        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.LEASED_RESERVED);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(RentedStatus.Rented, status.rentedStatus().getValue());
        }
        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.RESERVED);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(RentedStatus.Rented, status.rentedStatus().getValue());
        }
        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.APPROVED);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(RentedStatus.Unrented, status.rentedStatus().getValue());
        }

    }

    @Test
    public void testRendReadinessStatus() {
        {
            ILSUnit ilsUnit = new ILSUnit();
            ilsUnit.setComment("RentReady=false");
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unit.getInformation().add(unitInformation);

            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(RentReadiness.NeedsRepairs, status.rentReadinessStatus().getValue());
        }
        {
            ILSUnit ilsUnit = new ILSUnit();
            ilsUnit.setComment("RentReady=true");
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unit.getInformation().add(unitInformation);

            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(RentReadiness.RentReady, status.rentReadinessStatus().getValue());
        }
    }

    @Test
    public void testUnitRent() {
        // TODO
    }

    @Test
    public void marketRent() {
        // TODO 
    }

    @Test
    public void testRentDeltaAbsolute() {
        // TODO 
    }

    @Test
    public void rentEndDay() {

        /*
         * Applicable for 'Notice'
         */
        // TODO probably need to get this from lease 
    }

    /**
     * Applicable for 'Vacant', stores the first day when the unit has become vacant, used to calculate {@link #daysVacant()}
     */
    @Test
    public void vacantSince() {
        // TODO
    }

    @Test
    public void testRentedFromDay() {
        // TODO
    }

    @Test
    public void moveInDay() {
        // TODO 
    }

    public YardiUnitAvailabilityAdapter getAdapter() {
        return new YardiUnitAvailabilityAdapter();
    }
}
