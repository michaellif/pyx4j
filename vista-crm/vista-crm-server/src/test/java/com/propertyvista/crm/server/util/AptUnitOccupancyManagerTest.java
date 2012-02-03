/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

public class AptUnitOccupancyManagerTest {

    private static final boolean TEST_ON_MYSQL = false;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-DD");

    @Before
    public void setUp() {
        if (TEST_ON_MYSQL) {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        } else {
            VistaTestDBSetup.init();
        }

    }

    @Test
    public void testInsertSegment() throws ParseException {
        // weird stuff:
        Date date = DATE_FORMAT.parse("2012-12-31");
        LogicalDate logicalDate = new LogicalDate(date);

//        AptUnitOccupancy occupancy = EntityFactory.create(AptUnitOccupancy.class);
//
//        AptUnitOccupancySegment available = EntityFactory.create(AptUnitOccupancySegment.class);
//        available.status().setValue(Status.available);
//        available.dateFrom().setValue(AptUnitOccupancyManager.MIN_DATE);
//        available.dateTo().setValue(AptUnitOccupancyManager.MAX_DATE);
//
//        occupancy.timeline().add(available);
//
//        int i = AptUnitOccupancyManager.insertSegment(occupancy, asDate("2012-1-1"), AptUnitOccupancyManager.MAX_DATE, new SegmentInitializer() {
//            @Override
//            public void initRemainderOfTheSplitStatus(AptUnitOccupancySegment splitStatus) {
//            }
//
//            @Override
//            public void initAddedStatus(AptUnitOccupancySegment addedSegment) {
//                addedSegment.status().setValue(Status.leased);
//            }
//        });
//        assertValidOccupancyTimeline(occupancy.timeline());
//        assertOccupancyTimeline("available,MIN_DATE,2011-12-31;leased,2012-01-1,MAX_DATE", occupancy.timeline());

    }

    /**
     * @param encodedTimeline
     *            <code>(status,dateFrom,dateTo;)*</code>, dateFrom, dateTo date in yyyy-MM-DD format
     * @throws ParseException
     * @throws ClassCastException
     */
    public static void assertOccupancyTimeline(String encodedTimeline, List<AptUnitOccupancySegment> occupancyTimeline) throws ClassCastException,
            ParseException {
        String[] segments = encodedTimeline.split(";");

        for (int i = 0; i < occupancyTimeline.size(); ++i) {
            Assert.assertTrue(SimpleMessageFormat.format("timeline {0} has status at position {1} that shouldn't be there", occupancyTimeline, i),
                    shouldHaveStatus(i, segments));
            AptUnitOccupancySegment expected = parseSegment(segments[i].split(","));
            AptUnitOccupancySegment actual = occupancyTimeline.get(i);
            Assert.assertTrue(
                    SimpleMessageFormat.format("incorrect status at {0}:\nEXPECTED:\n{1}\nGOT:\n{2}\nTIMELINE:\n{3}\n", i, expected, actual, occupancyTimeline),
                    equalOccupancySegments(expected, actual));
        }
    }

    private static boolean equalOccupancySegments(AptUnitOccupancySegment expected, AptUnitOccupancySegment actual) {
        return//@formatter:off
                expected.dateFrom().getValue().getTime() == actual.dateFrom().getValue().getTime()
                 & expected.dateTo().getValue().getTime() ==  actual.dateTo().getValue().getTime()
                 & expected.status().getValue().equals(actual.status().getValue());
        //@formatter:on

    }

    private static final boolean shouldHaveStatus(int i, String[] segments) {
        return (i < segments.length) && !"".equals(segments[i]);
    }

    private static final AptUnitOccupancySegment parseSegment(String[] rawSegment) throws ClassCastException, ParseException {
        AptUnitOccupancySegment parsed = EntityFactory.create(AptUnitOccupancySegment.class);

        parsed.status().setValue(Status.valueOf(rawSegment[0]));
        parsed.dateFrom().setValue(asDate(rawSegment[1]));
        parsed.dateTo().setValue(asDate(rawSegment[2]));

        return parsed;
    }

    public static void assertValidOccupancyTimeline(List<AptUnitOccupancySegment> occupancyTimeline) {

    }

    private static LogicalDate asDate(String dateRepr) throws ParseException {
        if ("MAX_DATE".equals(dateRepr)) {
            return new LogicalDate(AptUnitOccupancyManager.MAX_DATE);
        } else if ("MIN_DATE".equals(dateRepr)) {
            return new LogicalDate(AptUnitOccupancyManager.MIN_DATE);
        } else {
            return new LogicalDate(DATE_FORMAT.parse(dateRepr));
        }
    }

}
