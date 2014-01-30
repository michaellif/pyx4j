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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;

/**
 * The fixture in this test is based on experimenting with Yardi Voyager and data form ILS GUESTCARD INTERFACE v 2.0,
 * received via UnitAvailability_Login().<br>
 * <br>
 * Regarding unit status: 'Ready' / 'Not Ready': although in Voyager this status seems to be set up via 'Functions'
 * (see more details in testRentReadinessStatus', it looks like what is sent from the interface is only determined by value in 'Rent Ready' checkbox, and this
 * when checkbox is set manually it doesn't update the 'unit status'
 * It's tested separately in special test, because it seems to be independent of other values.
 */
public class YardiUnitAvailabilityStatusAdapterTest {

    private final ILSUnit VACANT_UNRENTED;

    private ILSUnit VACANT_RENTED;

    private ILSUnit OCCUPIED_NO_NOTICE;

    private ILSUnit NOTICE_UNRENTED;

    private ILSUnit NOTICE_RENTED;

    private ILSUnit DOWN;

    private ILSUnit MODEL;

    public YardiUnitAvailabilityStatusAdapterTest() {
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
            unitInformation.setMarketRent(new BigDecimal("1111.00"));
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

            Availability availability = new Availability(); // set to move out date from Voyager 
            availability.setVacancyClass("Occupied");
            VacateDate vacateDate = new VacateDate();
            vacateDate.setYear("2014");
            vacateDate.setMonth("1");
            vacateDate.setDay("31");
            availability.setVacateDate(vacateDate);

            MadeReadyDate madeReadyDate = new MadeReadyDate(); // set to available from date from Voyager
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
            unitInformation.setMarketRent(new BigDecimal("889.00"));
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
            unitInformation.setUnitEcomomicStatus(Uniteconstatusinfo.MODEL);
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
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_UNRENTED);
            Assert.assertEquals(Vacancy.Vacant, status.vacancyStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_RENTED);
            Assert.assertEquals(Vacancy.Vacant, status.vacancyStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(OCCUPIED_NO_NOTICE);
            Assert.assertEquals(null, status.vacancyStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_UNRENTED);
            Assert.assertEquals(Vacancy.Notice, status.vacancyStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_RENTED);
            Assert.assertEquals(Vacancy.Notice, status.vacancyStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(DOWN);
            Assert.assertEquals(Vacancy.Vacant, status.vacancyStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(MODEL);
            Assert.assertEquals(Vacancy.Vacant, status.vacancyStatus().getValue());
        }
    }

    @Test
    public void testRentedStatus() {
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_UNRENTED);
            Assert.assertEquals(RentedStatus.Unrented, status.rentedStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_RENTED);
            Assert.assertEquals(RentedStatus.Rented, status.rentedStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(OCCUPIED_NO_NOTICE);
            Assert.assertEquals(null, status.rentedStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_UNRENTED);
            Assert.assertEquals(RentedStatus.Unrented, status.rentedStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_RENTED);
            Assert.assertEquals(RentedStatus.Rented, status.rentedStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(DOWN);
            Assert.assertEquals(RentedStatus.OffMarket, status.rentedStatus().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(MODEL);
            Assert.assertEquals(RentedStatus.OffMarket, status.rentedStatus().getValue());
        }
    }

    @Test
    public void testUnitRent() {
        // this is fetched from current or previous lease
    }

    @Test
    public void testMarketRent() {
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_UNRENTED);
            Assert.assertEquals(new BigDecimal("1000.00"), status.marketRent().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_RENTED);
            Assert.assertEquals(new BigDecimal("1111.00"), status.marketRent().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(OCCUPIED_NO_NOTICE);
            Assert.assertEquals(new BigDecimal("999.00"), status.marketRent().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_UNRENTED);
            Assert.assertEquals(new BigDecimal("888.00"), status.marketRent().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_RENTED);
            Assert.assertEquals(new BigDecimal("889.00"), status.marketRent().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(DOWN);
            Assert.assertEquals(new BigDecimal("777.00"), status.marketRent().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(MODEL);
            Assert.assertEquals(new BigDecimal("666.00"), status.marketRent().getValue());
        }
    }

    @Test
    public void testRentDeltaAbsolute() {
        // TODO 
    }

    /**
     * Applicable for 'Notice'
     */
    @Test
    public void testRentEndDay() {

        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_UNRENTED);
            Assert.assertEquals(null, status.rentEndDay().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_RENTED);
            Assert.assertEquals(null, status.rentEndDay().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(OCCUPIED_NO_NOTICE);
            Assert.assertEquals(null, status.rentEndDay().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_UNRENTED);
            Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2014-01-31")), status.rentEndDay().getValue());
        }

        // TODO probably need to get this from lease
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_RENTED);
            Assert.assertEquals(null, status.rentEndDay().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(DOWN);
            Assert.assertEquals(null, status.rentEndDay().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(MODEL);
            Assert.assertEquals(null, status.rentEndDay().getValue());
        }

    }

    /**
     * Applicable for 'Vacant', stores the first day when the unit has become vacant, used to calculate {@link #daysVacant()}
     */
    @Test
    public void testVacantSince() {
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_UNRENTED);
            Assert.assertEquals(new LogicalDate(DateUtils.detectDateformat("2014-01-31")), status.vacantSince().getValue());
        }

        // TODO maybe this information should be pulled from previous lease
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(VACANT_RENTED);
            Assert.assertEquals(null, status.vacantSince().getValue());
        }

        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(OCCUPIED_NO_NOTICE);
            Assert.assertEquals(null, status.vacantSince().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_UNRENTED);
            Assert.assertEquals(null, status.vacantSince().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(NOTICE_RENTED);
            Assert.assertEquals(null, status.vacantSince().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(DOWN);
            Assert.assertEquals(null, status.vacantSince().getValue());
        }
        {
            UnitAvailabilityStatus status = getAdapter().extractAvailabilityStatus(MODEL);
            Assert.assertEquals(null, status.vacantSince().getValue());
        }
    }

    @Test
    public void testRentedFromDay() {
        // TODO probably need to get that date from lease for 'RENTED' units
    }

    @Test
    public void testMoveInDay() {
        // TODO probably need to get that date from lease for 'RENTED' units
    }

    private YardiUnitAvailabilityStatusAdapter getAdapter() {
        return new YardiUnitAvailabilityStatusAdapter();
    }

}
