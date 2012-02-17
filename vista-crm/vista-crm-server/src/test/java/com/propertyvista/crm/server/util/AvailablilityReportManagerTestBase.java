/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Scoping;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManager;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl.NowSource;

public class AvailablilityReportManagerTestBase {

    protected static final String MAX_DATE = "MAX_DATE";

    protected static final String MIN_DATE = "MIN_DATE";

    private LogicalDate now = null;

    private AptUnitOccupancyManager manager = null;

    private AptUnit unit = null;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    List<AptUnitOccupancySegment> expectedTimeline = null;

    @Before
    public void setUp() {
        VistaTestDBSetup.init();
        TestLifecycle.testSession(new UserVisit(new Key(-101), "Neo"), VistaCrmBehavior.Occupancy, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        now = null;
        manager = null;

        Persistence.service().delete(new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class));
        Persistence.service().delete(new EntityQueryCriteria<Lease>(Lease.class));
        Persistence.service().delete(new EntityQueryCriteria<AptUnit>(AptUnit.class));

        unit = EntityFactory.create(AptUnit.class);
        unit.info().number().setValue("1");
        Persistence.service().merge(unit);

        expectedTimeline = new LinkedList<AptUnitOccupancySegment>();
    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    protected void now(String nowDate) {
        manager = null;
        now = asDate(nowDate);
    }

    protected AptUnitOccupancyManager getUOM() {
        if (manager == null) {
            if (now == null) {
                throw new IllegalStateException("can't create manager without the NOW date");
            }
            manager = new AptUnitOccupancyManagerImpl(unit, new NowSource() {
                @Override
                public LogicalDate getNow() {
                    return AvailablilityReportManagerTestBase.this.now;
                }
            });
        }
        return manager;
    }

    protected Lease createLease(String leaseFrom, String leaseTo) {
        if (unit != null) {
            Lease lease = EntityFactory.create(Lease.class);
            lease.unit().set(unit);
            lease.leaseFrom().setValue(asDate(leaseFrom));
            lease.leaseTo().setValue(asDate(leaseTo));
            Persistence.service().merge(lease);
            return lease;
        } else {
            throw new IllegalStateException("can't create a lease without a unit");
        }
    }

    protected void updateLease(Lease lease) {
        Persistence.service().merge(lease);
    }

    protected SetupBuilder setup() {
        return new SetupBuilder();
    }

    /**
     * Check that the occupancy timeline that is the DB is the same as was defined by calls to {@link #expect()}.<br/>
     * The actual use case of this function is to run it after series of {@link #expect()} assertions in order to check that the timeline does contain only
     * the expected segments (and nothing more).
     */
    protected void assertExpectedTimeline() {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
        criteria.asc(criteria.proto().dateFrom());
        List<AptUnitOccupancySegment> actualTimeline = Persistence.service().query(criteria);
        Assert.assertEquals("expected and actual timelines' number of segments don't match", expectedTimeline.size(), actualTimeline.size());

        Iterator<AptUnitOccupancySegment> a = actualTimeline.iterator();
        Iterator<AptUnitOccupancySegment> e = expectedTimeline.iterator();

        while (a.hasNext()) {
            AptUnitOccupancySegment actual = a.next();
            AptUnitOccupancySegment expected = e.next();
            assertEqualSegments(expected, actual);
        }

    }

    public static LogicalDate asDate(String dateRepr) {
        if ("MAX_DATE".equals(dateRepr)) {
            return new LogicalDate(AptUnitOccupancyManagerHelper.MAX_DATE);
        } else if ("MIN_DATE".equals(dateRepr)) {
            return new LogicalDate(AptUnitOccupancyManagerHelper.MIN_DATE);
        } else {
            try {
                return new LogicalDate(DATE_FORMAT.parse(dateRepr));
            } catch (ParseException e) {
                throw new Error("Invalid date format " + dateRepr);
            }
        }
    }

    protected static void assertEqualSegments(AptUnitOccupancySegment expected, AptUnitOccupancySegment actual) {
        String msg = "The actual and expected segments equality does not hold";
        Assert.assertEquals(msg, expected.unit().getPrimaryKey(), actual.unit().getPrimaryKey());
        Assert.assertEquals(msg, expected.status().getValue(), actual.status().getValue());
        Assert.assertEquals(msg, expected.dateFrom().getValue(), actual.dateFrom().getValue());
        Assert.assertEquals(msg, expected.dateTo().getValue(), actual.dateTo().getValue());
        Assert.assertEquals(msg, expected.offMarket().getValue(), actual.offMarket().getValue());
        if (expected.lease().isNull()) {
            Assert.assertEquals(msg, expected.lease().isNull(), actual.lease().isNull());
        } else {
            Assert.assertEquals(msg, expected.lease().getPrimaryKey(), actual.lease().getPrimaryKey());
        }
    }

    protected abstract static class SegmentBuilder<T extends SegmentBuilder<T>> {

        protected final AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);

        protected abstract T self();

        public T from(String dateRepr) {
            segment.dateFrom().setValue(asDate(dateRepr));
            return self();
        }

        public T fromTheBeginning() {
            segment.dateFrom().setValue(AptUnitOccupancyManagerHelper.MIN_DATE);
            return self();
        }

        public T to(String dateRepr) {
            segment.dateTo().setValue(asDate(dateRepr));
            return self();
        }

        public T toTheEndOfTime() {
            segment.dateTo().setValue(AptUnitOccupancyManagerHelper.MAX_DATE);
            return self();
        }

        public T status(Status status) {
            segment.status().setValue(status);
            return self();
        }

        public T withLease(Lease lease) {
            if (segment.status().getValue().equals(Status.leased) | segment.status().getValue().equals(Status.reserved)) {
                segment.lease().set(lease);
                return self();
            } else {
                throw new IllegalStateException("can't set lease when the unit is " + segment.status().getValue());
            }
        }

        public T withOffMarketType(OffMarketType offMarketType) {
            if (segment.status().getValue().equals(Status.offMarket)) {
                segment.offMarket().setValue(offMarketType);
                return self();
            } else {
                throw new IllegalStateException("can't set off market type when the unit is " + segment.status().getValue());
            }
        }

        protected void assertVaildSegment() {
            if (segment.status().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set", segment.status().getMeta().getCaption()));
            }
            if (segment.dateFrom().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set", segment.dateFrom().getMeta().getCaption()));
            }
            if (segment.dateTo().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set", segment.dateTo().getMeta().getCaption()));
            }
            if (segment.status().getValue().equals(Status.leased) & segment.lease().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set for {1} unit", segment.lease().getMeta().getCaption(),
                        Status.leased));
            }
            if (segment.status().getValue().equals(Status.reserved) & segment.lease().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set for {1} unit", segment.lease().getMeta().getCaption(),
                        Status.leased));
            }
            if (segment.status().getValue().equals(Status.offMarket) & segment.offMarket().isNull()) {
                throw new IllegalStateException("off market type was not set");
            }
        }

    }

    protected class SetupBuilder extends SegmentBuilder<SetupBuilder> {

        /**
         * execute the statement
         */
        public void x() {
            segment.unit().set(unit);
            assertVaildSegment();
            Persistence.service().merge(segment);
        }

        @Override
        protected SetupBuilder self() {
            return this;
        }
    }

    protected void computeAvailabilityOn(String dateRepr) {

    }

    protected AvailabilityStatusBuilder expectAvailability() {
        return new AvailabilityStatusBuilder();
    }

    protected class AvailabilityStatusBuilder {

        private LogicalDate statusDate = null;

        private Vacancy vacancy = null;

        private Scoping scoping = null;

        private LogicalDate rentStartsOn = null;

        private RentReadiness readiness = null;

        private RentedStatus rented = null;

        private LogicalDate rentEndsOn = null;

        public AvailabilityStatusBuilder on(String dateRepr) {
            statusDate = asDate(dateRepr);
            return this;
        }

        public AvailabilityStatusBuilder occupied() {
            return this;
        }

        public AvailabilityStatusBuilder vacancy(Vacancy vacancy) {
            this.vacancy = vacancy;
            return this;
        }

        public AvailabilityStatusBuilder rentEndsOn(String dateRepr) {
            this.rentEndsOn = asDate(dateRepr);
            return this;
        }

        public AvailabilityStatusBuilder rentStartsOn(String dateRepr) {
            this.rentStartsOn = asDate(dateRepr);
            return this;
        }

        public AvailabilityStatusBuilder scoping(Scoping scoping) {
            this.scoping = scoping;
            return this;
        }

        public AvailabilityStatusBuilder readiness(RentReadiness readiness) {
            this.readiness = readiness;
            return this;
        }

        public AvailabilityStatusBuilder rented() {
            this.rented = RentedStatus.Rented;
            return this;
        }

        public AvailabilityStatusBuilder notrented() {
            this.rented = RentedStatus.Unrented;
            return this;
        }

        public AvailabilityStatusBuilder offMarket() {
            this.rented = RentedStatus.OffMarket;
            return this;
        }

        public void x() {
            assertValid();
        }

        private void assertValid() {
            // TODO Auto-generated method stub

        }

    }
}
