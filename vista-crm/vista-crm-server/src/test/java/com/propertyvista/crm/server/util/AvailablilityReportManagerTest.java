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
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class AvailablilityReportManagerTest {

    private static AptUnit unit;

    @Before
    public void setUp() {
        VistaTestDBSetup.init();

        unit = EntityFactory.create(AptUnit.class);
    }

    @Test
    public void testComputeUnitAvailabliltiy() throws ParseException {
        LinkedList<AptUnitOccupancyEventEffect> mockupOccupancies = partitionToEvents(new LinkedList<MockupOccupancyStatus>(EntityCSVReciver.create(
                MockupOccupancyStatus.class).loadFile(IOUtils.resourceFileName("mockup-occupancy.csv", AvailablilityReportManagerTest.class))));

        LinkedList<MockupExpectedAvailabliltiyStatus> expected = new LinkedList<MockupExpectedAvailabliltiyStatus>(EntityCSVReciver.create(
                MockupExpectedAvailabliltiyStatus.class).loadFile(IOUtils.resourceFileName("expected-availability.csv", AvailablilityReportManagerTest.class)));

        assertTrue(!mockupOccupancies.isEmpty());

        AptUnitOccupancyEventEffect mockupOccupancy = null;
        while (!expected.isEmpty()) {
            MockupExpectedAvailabliltiyStatus expectedStatus = expected.pop();

            while (!mockupOccupancies.isEmpty() && (mockupOccupancies.peek().eventTimestamp.compareTo(expectedStatus.statusDate().getValue()) <= 0)) {
                mockupOccupancy = mockupOccupancies.poll();
            }
            UnitAvailabilityStatus status = AvailabilityReportManager.computeUnitAvailabilityStatus(expectedStatus.statusDate().getValue(),
                    mockupOccupancy.occupancy);
            assertIsOk(expectedStatus, status);
        }
    }

    private void assertIsOk(MockupExpectedAvailabliltiyStatus expectedStatus, UnitAvailabilityStatus status) {
        String msg = "test case #" + expectedStatus.testCaseNumber().getValue() + " failed for property: ";
        assertEquals(msg + status.statusDate().getPath().toString(), expectedStatus.statusDate().getValue(), status.statusDate().getValue());
        assertEquals(msg + status.vacancyStatus().getPath().toString(), expectedStatus.vacancyStatus().getValue(), status.vacancyStatus().getValue());
        assertEquals(msg + status.rentReadinessStatus().getPath().toString(), expectedStatus.rentReadinessStatus().getValue(), status.rentReadinessStatus()
                .getValue());
        assertEquals(msg + status.isScoped().getPath().toString(), expectedStatus.isScoped().getValue(), status.isScoped().getValue());
        assertEquals(msg + status.rentedStatus().getPath().toString(), expectedStatus.rentedStatus().getValue(), status.rentedStatus().getValue());
        assertEquals(msg + status.moveInDay().getPath().toString(), expectedStatus.moveInDay().getValue(), status.moveInDay().getValue());
        assertEquals(msg + status.moveOutDay().getPath().toString(), expectedStatus.moveOutDay().getValue(), status.moveOutDay().getValue());
    }

    private final AptUnitOccupancySegment convert(MockupOccupancyStatus mockup) {
        AptUnitOccupancySegment status = EntityFactory.create(AptUnitOccupancySegment.class);
        status.occupancy().unit().set(unit);
        status.status().set(mockup.status());
        status.dateFrom().set(mockup.dateFrom());
        status.dateTo().set(mockup.dateTo());

        if (Status.available.equals(mockup.status().getValue())) {

        } else if (Status.leased.equals(mockup.status().getValue())) {
            Lease lease = EntityFactory.create(Lease.class);
            lease.leaseFrom().setValue(status.dateFrom().getValue());
            lease.expectedMoveIn().setValue(status.dateFrom().getValue());
            lease.leaseTo().setValue(status.dateFrom().getValue());
            lease.expectedMoveOut().setValue(status.dateTo().getValue());
            status.lease().set(lease);
        } else if (Status.offMarket.equals(mockup.status().getValue())) {
            status.offMarket().setValue(mockup.offMarket().getValue());
        }

        return status;
    }

    private class AptUnitOccupancyEventEffect {

        private final LogicalDate eventTimestamp;

        private final List<AptUnitOccupancySegment> occupancy;

        public AptUnitOccupancyEventEffect(LogicalDate eventTimestamp, List<MockupOccupancyStatus> mockup) {
            assert eventTimestamp != null : "timestamp must not be null";
            assert mockup != null : "mockup occupancy must not be null";
            assert !mockup.isEmpty() : "mockup occupancy must not be empty";

            this.eventTimestamp = eventTimestamp;
            this.occupancy = new ArrayList<AptUnitOccupancySegment>();
            for (MockupOccupancyStatus mockupStatus : mockup) {
                this.occupancy.add(convert(mockupStatus));
            }
        }

        @Override
        public String toString() {
            return "(" + eventTimestamp.toString() + ": " + occupancy.toString() + ")";
        }
    }

    private LinkedList<AptUnitOccupancyEventEffect> partitionToEvents(LinkedList<MockupOccupancyStatus> mockup) {
        LinkedList<AptUnitOccupancyEventEffect> states = new LinkedList<AptUnitOccupancyEventEffect>();
        LogicalDate lastTimestamp = null;
        while (!mockup.isEmpty()) {
            if (lastTimestamp == null || !lastTimestamp.equals(mockup.peek().statusDate().getValue())) {
                lastTimestamp = mockup.peek().statusDate().getValue();
            }
            List<MockupOccupancyStatus> mockupState = new ArrayList<MockupOccupancyStatus>();
            while (!mockup.isEmpty() && lastTimestamp.equals(mockup.peek().statusDate().getValue())) {
                MockupOccupancyStatus temp = mockup.pop();
                temp.statusDate().setValue(temp.statusDate().getValue());
                temp.dateFrom().setValue(temp.dateFrom().getValue());
                temp.dateTo().setValue(temp.dateTo().getValue());
                mockupState.add(temp);
            }
            states.add(new AptUnitOccupancyEventEffect(lastTimestamp, mockupState));
        }
        return states;
    }
}
