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

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.MadeReadyDate;
import com.yardi.entity.ils.VacateDate;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.mits.Uniteconstatusinfo;
import com.yardi.entity.mits.Unitleasestatusinfo;
import com.yardi.entity.mits.Unitoccpstatusinfo;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;

/**
 * The fixture in this test is based on experimenting with Yardi Voyager and data form ILS GUESTCARD INTERFACE v 2.0,
 * received via UnitAvailability_Login().<br>
 * <br>
 * Regarding unit status: 'Ready' / 'Not Ready': although in Voyager this status seems to be set up via 'Functions'
 * (see more details in testRentReadinessStatus', it looks like what is sent from the interface is only determined by value in 'Rent Ready' checkbox, and this
 * when checkbox is set manually it doesn't update the 'unit status'
 * It's tested separately in special test, because it seems to be independent of other values.
 */

public class YardiUnitAvailabilityAdapterTest {

    private final ILSUnit VACANT_UNRENTED;

    private ILSUnit VACANT_RENTED;

    private ILSUnit OCCUPIED_NO_NOTICE;

    private ILSUnit NOTICE_UNRENTED;

    private ILSUnit NOTICE_RENTED;

    private ILSUnit DOWN;

    private ILSUnit MODEL;

    public YardiUnitAvailabilityAdapterTest() {
        {

            VACANT_UNRENTED = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setMarketRent(new BigDecimal("1000.00"));
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.RESIDENTIAL);
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.VACANT);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.AVAILABLE);
            unit.getInformation().add(unitInformation);
            VACANT_UNRENTED.setUnit(unit);

            Availability availability = new Availability();
            availability.setVacancyClass("Unoccupied");
            VacateDate vacateDate = new VacateDate();
            vacateDate.setYear("2014");
            vacateDate.setMonth("1");
            vacateDate.setDay("31");

            availability.setVacateDate(vacateDate);
            MadeReadyDate madeReadyDate = new MadeReadyDate();
            madeReadyDate.setYear("2014");
            madeReadyDate.setMonth("2");
            madeReadyDate.setDay("1");
            availability.setMadeReadyDate(madeReadyDate);

            VACANT_UNRENTED.setAvailability(availability);
        }
        {
            VACANT_RENTED = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setMarketRent(new BigDecimal("1000.00"));
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.RESIDENTIAL);
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.VACANT);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.LEASED_RESERVED);
            unit.getInformation().add(unitInformation);
            VACANT_RENTED.setUnit(unit);

            // RENTED DOESN'T HAVE AVAILABILITY!!!
        }
        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setMarketRent(new BigDecimal("999.00"));
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.RESIDENTIAL);
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.LEASED);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            // DOESN'T HAVE AVAILABILITY

            OCCUPIED_NO_NOTICE = ilsUnit;
        }
        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setMarketRent(new BigDecimal("888.00"));
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.RESIDENTIAL);
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.ON_NOTICE);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            Availability availability = new Availability();
            availability.setVacancyClass("Occupied");
            VacateDate vacateDate = new VacateDate();
            vacateDate.setYear("2014");
            vacateDate.setMonth("1");
            vacateDate.setDay("31");

            availability.setVacateDate(vacateDate);
            MadeReadyDate madeReadyDate = new MadeReadyDate();
            madeReadyDate.setYear("2014");
            madeReadyDate.setMonth("2");
            madeReadyDate.setDay("1");
            availability.setMadeReadyDate(madeReadyDate);

            ilsUnit.setAvailability(availability);

            NOTICE_UNRENTED = ilsUnit;
        }

        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setMarketRent(new BigDecimal("888.00"));
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.RESIDENTIAL);
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.OCCUPIED);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.LEASED_RESERVED);
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            // NO AVAILABILITY!!!!

            NOTICE_RENTED = ilsUnit;
        }

        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setMarketRent(new BigDecimal("777.00"));
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.DOWN);
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.VACANT);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.OTHER);
            unitInformation.setUnitLeasedStatusDescription("Non Revenue Generating Unit");
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            // NO AVAILABILITY!!!!

            DOWN = ilsUnit;
        }

        {
            ILSUnit ilsUnit = new ILSUnit();
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unitInformation.setMarketRent(new BigDecimal("666.00"));
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.DOWN);
            unitInformation.setUnitOccupancyStatus(Unitoccpstatusinfo.VACANT);
            unitInformation.setUnitLeasedStatus(Unitleasestatusinfo.OTHER);
            unitInformation.setUnitLeasedStatusDescription("Non Revenue Generating Unit");
            unit.getInformation().add(unitInformation);
            ilsUnit.setUnit(unit);

            // NO AVAILABILITY!!!!

            MODEL = ilsUnit;
        }

    }

    @Test
    public void testRendReadinessStatus() {
        // TODO: I haven't found a way to set up equivalent to 'Needs Repairs' or 'RenovationInProgress' in Voyager
        // from what I've seen there are two strings that appear under Unit/Occupancy/Unit Status: 'Ready' or 'Not Ready'
        // 'Not Ready' - is set up automatically after Lease:Functions:Move Out
        // 'Ready' - is set via: Community Manager Dashboard: Maintenance: Pending Make Ready: Check Make Ready on the desired unit

        {
            ILSUnit ilsUnit = new ILSUnit();
            ilsUnit.setComment("RentReady=false");
            Unit unit = new Unit();
            Information unitInformation = new Information();
            unit.getInformation().add(unitInformation);

            ilsUnit.setUnit(unit);

            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(ilsUnit);
            Assert.assertEquals(null, status.rentReadinessStatus().getValue());
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
    public void testVacancyStatus() {

    }

    @Test
    public void testRentedStatus() {

    }

    @Test
    public void testUnitRent() {
        // TODO
    }

    @Test
    public void testMarketRent() {
        // TODO 
    }

    @Test
    public void testRentDeltaAbsolute() {
        // TODO 
    }

    @Test
    public void testRentEndDay() {

        /*
         * Applicable for 'Notice'
         */
        // TODO probably need to get this from lease 
    }

    /**
     * Applicable for 'Vacant', stores the first day when the unit has become vacant, used to calculate {@link #daysVacant()}
     */
    @Test
    public void testVacantSince() {
        // TODO
    }

    @Test
    public void testRentedFromDay() {
        // TODO
    }

    @Test
    public void testMoveInDay() {
        // TODO 
    }

    public YardiUnitAvailabilityAdapter getAdapter() {
        return new YardiUnitAvailabilityAdapter();
    }

}
