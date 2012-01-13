/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadinessStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.VacancyStatus;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class AvailablilityReportManagerTest {

    private static final boolean TEST_ON_MYSQL = false;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

    private static final LogicalDate MIN_DATE = new LogicalDate(Long.MIN_VALUE);

    private static final LogicalDate MAX_DATE = new LogicalDate(Long.MAX_VALUE);

    private AptUnit unit;

    private Lease lease1;

    private Lease lease2;

    @Before
    public void setUp() {
        if (TEST_ON_MYSQL) {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        } else {
            VistaTestDBSetup.init();
        }

        unit = EntityFactory.create(AptUnit.class);

        lease1 = EntityFactory.create(Lease.class);

        lease2 = EntityFactory.create(Lease.class);
    }

    @Test
    public void testComputeUnitAvailabliltiy() throws ParseException {
        List<AptUnitOccupancy> occupancy = new ArrayList<AptUnitOccupancy>();

        AptUnitOccupancy available = EntityFactory.create(AptUnitOccupancy.class);
        available.dateFrom().setValue(MIN_DATE);
        available.dateTo().setValue(MAX_DATE);
        available.status().setValue(Status.available);

        occupancy.add(available);

        LogicalDate date = new LogicalDate(DATE_FORMAT.parse("2010.1.1"));

        UnitAvailabilityStatus status = AvailabilityReportManager.computeUnitAvailabilityStatus(date, occupancy);
        assertEquals(VacancyStatus.Vacant, status.vacancyStatus().getValue());
        assertEquals(RentReadinessStatus.RentReady, status.rentReadinessStatus().getValue());
        assertEquals(true, status.isScoped().getValue());
        assertEquals(RentedStatus.Unrented, status.rentedStatus().getValue());
    }
}
