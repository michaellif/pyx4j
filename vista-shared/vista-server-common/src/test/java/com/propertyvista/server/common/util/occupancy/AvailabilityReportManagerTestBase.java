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
package com.propertyvista.server.common.util.occupancy;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.biz.occupancy.AvailabilityReportManager;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
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
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class AvailabilityReportManagerTestBase {

    private AptUnit unit = null;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    protected Key unitId = null;

    @Before
    public void setUp() {
        VistaTestDBSetup.init();
        TestLifecycle.testSession(new UserVisit(new Key(-101), "Neo"), VistaCrmBehavior.Occupancy, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        Persistence.service().delete(new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class));
        Persistence.service().delete(new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class));
        Persistence.service().delete(new EntityQueryCriteria<Lease>(Lease.class));
        Persistence.service().delete(new EntityQueryCriteria<AptUnit>(AptUnit.class));

        unit = EntityFactory.create(AptUnit.class);
        unit.info().number().setValue("1");
        unit.belongsTo().propertyCode().setValue("2");
        Persistence.service().merge(unit.belongsTo());
        Persistence.service().merge(unit);
        unitId = unit.getPrimaryKey();
    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    protected void now(String nowDate) {
        Persistence.service().setTransactionSystemTime(asDate(nowDate));
    }

    protected void computeAvailabilityOn(String dateRepr) {
        new AvailabilityReportManager(unit).generateUnitAvailablity(asDate(dateRepr));
    }

    protected AvailabilityStatusBuilder expectAvailability() {
        return new AvailabilityStatusBuilder();
    }

    protected OccupancyFacade getUOM() {
        return ServerSideFactory.create(OccupancyFacade.class);
    }

    protected Lease createLease(String leaseFrom, String moveIn, String leaseTo) {
        if (unit != null) {
            Lease lease = EntityFactory.create(Lease.class);
            lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
            lease.unit().set(unit);
            lease.leaseFrom().setValue(asDate(leaseFrom));
            lease.version().expectedMoveIn().setValue(asDate(moveIn));
            lease.leaseTo().setValue(asDate(leaseTo));
            lease.saveAction().setValue(SaveAction.saveAsFinal);
            Persistence.service().persist(lease);
            return lease;
        } else {
            throw new IllegalStateException("can't create a lease without a unit");
        }
    }

    protected SetupBuilder setup() {
        return new SetupBuilder();
    }

    public static LogicalDate asDate(String dateRepr) {
        if ("MAX_DATE".equals(dateRepr)) {
            return new LogicalDate(OccupancyFacade.MAX_DATE);
        } else if ("MIN_DATE".equals(dateRepr)) {
            return new LogicalDate(OccupancyFacade.MIN_DATE);
        } else {
            try {
                return new LogicalDate(DATE_FORMAT.parse(dateRepr));
            } catch (ParseException e) {
                throw new Error("Invalid date format " + dateRepr);
            }
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
            segment.dateFrom().setValue(OccupancyFacade.MIN_DATE);
            return self();
        }

        public T to(String dateRepr) {
            segment.dateTo().setValue(asDate(dateRepr));
            return self();
        }

        public T toTheEndOfTime() {
            segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
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

    protected class AvailabilityStatusBuilder {

        private LogicalDate statusDate = null;

        private Vacancy vacancy = null;

        private Scoping scoping = null;

        private LogicalDate rentStartsOn = null;

        private RentReadiness readiness = null;

        private RentedStatus rented = null;

        private LogicalDate rentEndsOn = null;

        private LogicalDate moveinDay;

        private LogicalDate vacantSince;

        public AvailabilityStatusBuilder on(String dateRepr) {
            statusDate = asDate(dateRepr);
            return this;
        }

        public AvailabilityStatusBuilder occupied() {
            return this;
        }

        public AvailabilityStatusBuilder vacant(String vacantSince) {
            this.vacancy = Vacancy.Vacant;
            this.vacantSince = asDate(vacantSince);
            return this;
        }

        public AvailabilityStatusBuilder vacant() {
            this.vacancy = Vacancy.Vacant;
            return this;
        }

        public AvailabilityStatusBuilder notice(String rentEndsOn) {
            this.vacancy = Vacancy.Notice;
            this.rentEndsOn = asDate(rentEndsOn);
            return this;
        }

        public AvailabilityStatusBuilder scoped(RentReadiness status) {
            this.scoping = Scoping.Scoped;
            this.readiness = status;
            return this;
        }

        public AvailabilityStatusBuilder unscoped() {
            this.scoping = Scoping.Unscoped;
            return this;
        }

        public AvailabilityStatusBuilder rented(String rentStartsOn, String moveInDay) {
            this.rented = RentedStatus.Rented;
            this.rentStartsOn = asDate(rentStartsOn);
            this.moveinDay = asDate(moveInDay);
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

            UnitAvailabilityStatus expected = EntityFactory.create(UnitAvailabilityStatus.class);
            expected.statusDate().setValue(statusDate);
            expected.vacancyStatus().setValue(vacancy);
            expected.vacantSince().setValue(vacantSince);
            expected.rentEndDay().setValue(rentEndsOn);
            expected.scoping().setValue(scoping);
            expected.rentReadinessStatus().setValue(readiness);
            expected.rentedStatus().setValue(rented);
            expected.moveInDay().setValue(moveinDay);
            expected.rentedFromDay().setValue(rentStartsOn);

            EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().statusDate(), statusDate));
            criteria.add(PropertyCriterion.eq(criteria.proto().vacancyStatus(), vacancy));
            criteria.add(PropertyCriterion.eq(criteria.proto().vacantSince(), vacantSince));
            criteria.add(PropertyCriterion.eq(criteria.proto().rentEndDay(), rentEndsOn));
            criteria.add(PropertyCriterion.eq(criteria.proto().scoping(), scoping));
            criteria.add(PropertyCriterion.eq(criteria.proto().rentReadinessStatus(), readiness));
            criteria.add(PropertyCriterion.eq(criteria.proto().rentedStatus(), rented));
            criteria.add(PropertyCriterion.eq(criteria.proto().moveInDay(), moveinDay));
            criteria.add(PropertyCriterion.eq(criteria.proto().rentedFromDay(), rentStartsOn));

            UnitAvailabilityStatus actual = Persistence.service().retrieve(criteria);
            Assert.assertNotNull("Expected status " + expected.toString() + " was not found in the DB", actual);
        }

        private void assertValid() {
            // TODO i think i'm too lazy for this right now
        }

    }
}
