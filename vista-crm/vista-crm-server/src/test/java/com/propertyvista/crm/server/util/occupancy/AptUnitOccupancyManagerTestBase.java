/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util.occupancy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.junit.Assert;
import org.junit.Before;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class AptUnitOccupancyManagerTestBase {

    private LogicalDate now = null;

    private AptUnitOccupancyManager manager = null;

    private final AptUnit unit = null;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void setup() {
        VistaTestDBSetup.init();

        now = null;
        manager = null;
        Persistence.service().delete(new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class));
        Persistence.service().delete(new EntityQueryCriteria<Lease>(Lease.class));
        Persistence.service().delete(new EntityQueryCriteria<AptUnit>(AptUnit.class));
        Persistence.service().delete(new EntityQueryCriteria<Building>(Building.class));

        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.info().number().setValue("1");
        Persistence.service().persist(unit);
    }

    public void setup(String dateFrom, String dateTo, Status status) throws ClassCastException, ParseException {
        setup(dateFrom, dateTo, status, null);
    }

    public void setupOffMarket(String dateFrom, String dateTo, OffMarketType offMarketType) throws ClassCastException, ParseException {
        setup(dateFrom, dateTo, Status.offMarket, offMarketType);
    }

    private void setup(String dateFrom, String dateTo, Status status, OffMarketType offMarketType) throws ClassCastException, ParseException {
        AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
        segment.dateFrom().setValue(asDate(dateFrom));
        segment.dateTo().setValue(asDate(dateTo));
        segment.status().setValue(status);
        segment.offMarket().setValue(offMarketType);
        segment.lease().setValue(null);

        Persistence.service().merge(segment);
    }

    public void now(String nowDate) throws ParseException {
        manager = null;
        now = asDate(nowDate);
    }

    public AptUnitOccupancyManager getUOM() {
        if (manager == null) {
            if (now == null) {
                throw new IllegalStateException("can't create manager without the NOW date");
            }
            manager = new AptUnitOccupancyManagerImpl();
            // TODO set now and unit;
        }
        return manager;
    }

    public Lease createLease() {
        if (unit != null) {
            Lease lease = EntityFactory.create(Lease.class);
            lease.unit().set(unit);
            Persistence.service().persist(lease);
            return lease;
        } else {
            throw new IllegalStateException("can't create lease without unit");
        }
    }

    public void expect(String from, String to, Status status) throws ParseException {
        expect(from, to, status, null);
    }

    public void expectOffMarket(String from, String to, OffMarketType offMarketType) throws ParseException {
        expect(from, to, Status.offMarket, offMarketType);
    }

    private void expect(String from, String to, Status status, OffMarketType offMarketType) throws ParseException {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().dateFrom(), asDate(from)));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), asDate(to)));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), status));
        criteria.add(PropertyCriterion.eq(criteria.proto().offMarket(), offMarketType));

        AptUnitOccupancySegment actual = Persistence.service().retrieve(criteria);
        Assert.assertNotNull(
                SimpleMessageFormat.format("the expected occupancy segment was not found in the DB ({0}, {1}: {2})", from, to, status == Status.offMarket ? ""
                        + status + " - " + offMarketType : status), actual);
    }

    /**
     * Assert that this occupancy is valid, for validity criteria see {@link #isValidOccupancyTimeline(List)}.
     * 
     * @param occupancyTimeline
     */
    public static void assertValidOccupancyTimeline(List<AptUnitOccupancySegment> occupancyTimeline) {
        Error error = isValidOccupancyTimeline(occupancyTimeline);
        if (error != null) {
            throw new AssertionFailedError(error.getMessage());
        }
    }

    /**
     * Validate that the {@code occupancyTimeline} satisfies the following constraints:<br/>
     * 
     * <li>The segments must not overlap.</li><br/>
     * <li>The segments must cover the whole time.</li><br/>
     * <li>The segments must be in ascending order with respect to {@link AptUnitOccupancySegment#dateFrom()}</li><br/>
     * <li>TBD...</li>
     * 
     * @param occupancyTimeline
     * @return
     *         <li><code>null</code> if the occupancy that was provided is valid</li>
     * 
     *         <li><code>{@link Error}</code> that hopefully contains a message describing what is wrong, if some of the constraints are not satisfied</li>
     */
    public static Error isValidOccupancyTimeline(List<AptUnitOccupancySegment> occupancyTimeline) {
        return null; // TODO implement isValidOccupancyTimeline and documentation
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
                expected.dateFrom().equals(actual.dateFrom())
                 & expected.dateTo().equals(actual.dateTo())
                 & expected.status().equals(actual.status());
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

    public static LogicalDate asDate(String dateRepr) throws ParseException {
        if ("MAX_DATE".equals(dateRepr)) {
            return new LogicalDate(AptUnitOccupancyManagerHelper.MAX_DATE);
        } else if ("MIN_DATE".equals(dateRepr)) {
            return new LogicalDate(AptUnitOccupancyManagerHelper.MIN_DATE);
        } else {
            return new LogicalDate(DATE_FORMAT.parse(dateRepr));
        }
    }
}
