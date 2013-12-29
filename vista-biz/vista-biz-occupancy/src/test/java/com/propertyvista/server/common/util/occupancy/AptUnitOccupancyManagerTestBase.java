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
package com.propertyvista.server.common.util.occupancy;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.pmc.IntegrationSystem;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.helper.LightWeightLeaseManagement;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.MockManager;
import com.propertyvista.test.mock.models.PmcDataModel;

public class AptUnitOccupancyManagerTestBase {

    private AptUnit unit = null;

    protected Key unitId = null;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    List<AptUnitOccupancySegment> expectedTimeline = null;

    private static int gensymCount = 1;

    @Before
    public void setUp() {
        VistaTestDBSetup.init();

        TestLifecycle.testSession(new UserVisit(new Key(-101), "Neo"), VistaCrmBehavior.Occupancy, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        SystemDateManager.setDate(asDate("1900-01-01"));

        preloadData();

        generateIdAssignmentPolicy();

        ARCode arCode = EntityFactory.create(ARCode.class);
        arCode.type().setValue(ARCode.Type.Residential);
        arCode.name().setValue("residential unit for every child");
        Persistence.service().merge(arCode);

        unit = EntityFactory.create(AptUnit.class);
        unit.info().number().setValue("" + (++gensymCount));
        unit.building().propertyCode().setValue("" + (++gensymCount));
        unit.building().integrationSystemId().setValue(IntegrationSystem.internal);
        Persistence.service().merge(unit.building());
        Persistence.service().merge(unit);

        Service service = EntityFactory.create(Service.class);
        service.code().set(arCode);
        service.version().name().setValue("Residential Unit Service");
        service.version().description().setValue("Residential Unit Descriptio");

        ProductItem serviceItem = EntityFactory.create(ProductItem.class);
        serviceItem.element().set(unit);
        serviceItem.price().setValue(new BigDecimal("1000.00"));
        serviceItem.name().setValue(arCode.name().getValue());
        serviceItem.description().setValue("a mockup unit");

        service.version().items().add(serviceItem);

        unit.building().productCatalog().services().add(service);
        Persistence.service().merge(service);
        Persistence.service().merge(unit.building().productCatalog());
        unitId = unit.getPrimaryKey();

        expectedTimeline = new LinkedList<AptUnitOccupancySegment>();
        Persistence.service().commit();
        SystemDateManager.resetDate();
    }

    protected void preloadData() {
        preloadData(new MockConfig());
    }

    protected void preloadData(final MockConfig config) {

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<MockManager, RuntimeException>() {

            @Override
            public MockManager execute() {

                MockManager mockManager = new MockManager(config);
                for (Class<? extends MockDataModel<?>> modelType : getMockModelTypes()) {
                    mockManager.addModel(modelType);
                }

                return mockManager;
            }
        });

    }

    protected List<Class<? extends MockDataModel<?>>> getMockModelTypes() {
        List<Class<? extends MockDataModel<?>>> models = new ArrayList<Class<? extends MockDataModel<?>>>();
        models.add(PmcDataModel.class);
        return models;
    }

    protected void generateIdAssignmentPolicy() {
        OrganizationPoliciesNode orgNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().persist(orgNode);

        IdAssignmentPolicy policy = EntityFactory.create(IdAssignmentPolicy.class);
        policy.node().set(orgNode);

        IdAssignmentItem item = EntityFactory.create(IdAssignmentItem.class);
        item.target().setValue(IdTarget.lease);
        item.type().setValue(IdAssignmentType.generatedNumber);

        policy.items().add(item);
        Persistence.service().persist(policy);
    }

    @After
    public void tearDown() {
        try {
            Persistence.service().commit();
        } finally {
            TestLifecycle.tearDown();
        }
    }

    protected AptUnit unitStub() {
        unit = EntityFactory.create(AptUnit.class);
        unit.setPrimaryKey(unitId);
        return unit;
    }

    protected void now(String nowDate) {
        SystemDateManager.setDate(asDate(nowDate));
    }

    protected OccupancyFacade getUOM() {
        return ServerSideFactory.create(OccupancyFacade.class);
    }

    protected Lease createLease(String leaseFrom, String leaseTo) {
        if (unit != null) {
            Lease lease = LightWeightLeaseManagement.create(Lease.Status.Application);
            lease.unit().set(unit);
            lease.leaseFrom().setValue(asDate(leaseFrom));
            lease.currentTerm().termFrom().setValue(asDate(leaseFrom));
            lease.leaseTo().setValue(asDate(leaseTo));
            lease.currentTerm().termTo().setValue(asDate(leaseTo));
            LightWeightLeaseManagement.persist(lease, false);
            return lease;
        } else {
            throw new IllegalStateException("can't create a lease without a unit");
        }
    }

    protected void updateLease(Lease lease) {
        LightWeightLeaseManagement.persist(lease, false);
    }

    protected ExpectBuilder expect() {
        return new ExpectBuilder(expectedTimeline);
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
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.asc(criteria.proto().dateFrom());
        List<AptUnitOccupancySegment> actualTimeline = Persistence.service().query(criteria);
        Assert.assertEquals("expected and actual timelines' number of segments don't match:\n actual timeline is:\n" + createSegmentsView(actualTimeline)
                + "\n", expectedTimeline.size(), actualTimeline.size());

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

    protected void assertUnitIsAvailableFrom(LogicalDate date) {
        Assert.assertEquals(date, Persistence.service().retrieve(AptUnit.class, unit.getPrimaryKey())._availableForRent().getValue());
    }

    protected void assertUnitIsAvailableFrom(String dateRepr) {
        assertUnitIsAvailableFrom(new LogicalDate(asDate(dateRepr)));
    }

    protected void assertUnitIsNotAvailable() {
        assertUnitIsAvailableFrom((LogicalDate) null);
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
            Assert.assertEquals(msg, expected.lease().getPrimaryKey().asCurrentKey(), actual.lease().getPrimaryKey().asCurrentKey());
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
            if (segment.status().getValue().equals(Status.occupied) | segment.status().getValue().equals(Status.reserved)
                    | segment.status().getValue().equals(Status.migrated)) {
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
            if (segment.status().getValue().equals(Status.occupied) & segment.lease().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set for {1} unit", segment.lease().getMeta().getCaption(),
                        Status.occupied));
            }
            if (segment.status().getValue().equals(Status.reserved) & segment.lease().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set for {1} unit", segment.lease().getMeta().getCaption(),
                        Status.occupied));
            }
            if (segment.status().getValue().equals(Status.migrated) & segment.lease().isNull()) {
                throw new IllegalStateException(SimpleMessageFormat.format("{0} was not set for {1} unit", segment.lease().getMeta().getCaption(),
                        Status.occupied));
            }
            if (segment.status().getValue().equals(Status.offMarket) & segment.offMarket().isNull()) {
                throw new IllegalStateException("off market type was not set");
            }
        }

    }

    private static String createSegmentsView(List<AptUnitOccupancySegment> actualSegments) {
        List<String> segmentViews = new ArrayList<String>();
        for (AptUnitOccupancySegment segment : actualSegments) {
            segmentViews.add(createSegmentView(segment));
        }
        return StringUtils.join(segmentViews, "\n");
    }

    private static String createSegmentView(AptUnitOccupancySegment segment) {
        String date = "[" + segment.dateFrom().getValue() + ", " + segment.dateTo().getValue() + "] ";
        String status = segment.status().getValue().toString();
        if (segment.status().getValue() == Status.offMarket) {
            status += " - " + segment.offMarket().getValue().toString();
        }
        return date + status;
    }

    protected class ExpectBuilder extends SegmentBuilder<ExpectBuilder> {

        private final List<AptUnitOccupancySegment> expectedTimeline;

        protected ExpectBuilder(List<AptUnitOccupancySegment> expectedTimeline) {
            this.expectedTimeline = expectedTimeline;
        }

        /**
         * execute the statement.
         */
        public void x() {
            segment.unit().set(unit);
            assertVaildSegment();
            expectedTimeline.add(segment);

            EntityQueryCriteria<AptUnitOccupancySegment> actualOccupancyCriteria = new EntityQueryCriteria<AptUnitOccupancySegment>(
                    AptUnitOccupancySegment.class);
            actualOccupancyCriteria.add(PropertyCriterion.eq(actualOccupancyCriteria.proto().unit(), unit));
            actualOccupancyCriteria.asc(actualOccupancyCriteria.proto().dateFrom());
            List<AptUnitOccupancySegment> actualSegments = Persistence.service().query(actualOccupancyCriteria);
            String actualSegmentsView = createSegmentsView(actualSegments);

            EntityQueryCriteria<AptUnitOccupancySegment> actualSegmentCriteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);

            actualSegmentCriteria.add(PropertyCriterion.eq(actualSegmentCriteria.proto().dateFrom(), segment.dateFrom().getValue()));
            actualSegmentCriteria.add(PropertyCriterion.eq(actualSegmentCriteria.proto().dateTo(), segment.dateTo().getValue()));
            actualSegmentCriteria.add(PropertyCriterion.eq(actualSegmentCriteria.proto().status(), segment.status().getValue()));
            actualSegmentCriteria.add(PropertyCriterion.eq(actualSegmentCriteria.proto().offMarket(), segment.offMarket().getValue()));
            actualSegmentCriteria.add(PropertyCriterion.eq(actualSegmentCriteria.proto().lease(), segment.lease().isNull() ? null : segment.lease()));

            AptUnitOccupancySegment actual = Persistence.service().retrieve(actualSegmentCriteria);
            Assert.assertNotNull(SimpleMessageFormat.format("the expected occupancy segment was not found in the DB:\n {0}\n the actual DB occupancy is:\n{1}",
                    createSegmentView(segment), actualSegmentsView), actual);
        }

        @Override
        protected ExpectBuilder self() {
            return this;
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

}
