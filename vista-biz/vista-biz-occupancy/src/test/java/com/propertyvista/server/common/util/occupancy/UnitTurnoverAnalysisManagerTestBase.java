/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.SplittingHandler;
import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacade;
import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoverStats;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

// TODO enhance the test base to allow test cases with multiple buildings
public class UnitTurnoverAnalysisManagerTestBase {

    private Building building;

    private Floorplan floorplan;

    private final UnitTurnoverAnalysisFacade theMan;

    protected UnitTurnoverAnalysisManagerTestBase(UnitTurnoverAnalysisFacade manager) {
        theMan = manager;
    }

    @Before
    public void setUp() {
        VistaTestDBSetup.init();
        TestLifecycle.testSession(new UserVisit(new Key(-101), "Neo"), VistaCrmBehavior.Occupancy, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        // clear tables
        //Persistence.service().delete(EntityQueryCriteria.create(Tenant.class));
        //Persistence.service().delete(EntityQueryCriteria.create(TenantUser.class));
        Persistence.service().delete(EntityQueryCriteria.create(UnitTurnoverStats.class));
        Persistence.service().delete(EntityQueryCriteria.create(AptUnitOccupancySegment.class));
        Persistence.service().delete(EntityQueryCriteria.create(UnitAvailabilityStatus.class));
        {
            EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
            leaseCriteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
            Persistence.service().delete(leaseCriteria);
        }
        {
            EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
            leaseCriteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            Persistence.service().delete(leaseCriteria);
        }
        Persistence.service().delete(EntityQueryCriteria.create(AptUnit.class));
        Persistence.service().delete(EntityQueryCriteria.create(Floorplan.class));
        Persistence.service().delete(EntityQueryCriteria.create(ProductCatalog.class));
        Persistence.service().delete(EntityQueryCriteria.create(Building.class));

        // define common domain objects
        building = EntityFactory.create(Building.class);
        building.propertyCode().setValue("B1");
        building.info().name().setValue("building-1");
        Persistence.secureSave(building);

        floorplan = EntityFactory.create(Floorplan.class);
        floorplan.bathrooms().setValue(1);
        floorplan.bedrooms().setValue(2);
        floorplan.name().setValue("floorplan-1");
        floorplan.building().set(building);
        Persistence.secureSave(floorplan);

    }

    @After
    public void tearDown() {

    }

    /**
     * Creates lease and relevant occupancy segments, and then invokes the turnover recalculation routine of the turnover manager
     * 
     * @param unit
     * @param dateFrom
     * @param dateTo
     */
    protected void lease(AptUnit unit, String dateFrom, String dateTo) {
        Customer tenant = EntityFactory.create(Customer.class);
        CustomerUser user = EntityFactory.create(CustomerUser.class);
        user.name().setValue("tenant " + dateFrom);
        Persistence.service().merge(user);
        tenant.user().set(user);
        Persistence.service().merge(tenant);

        final Lease lease = EntityFactory.create(Lease.class);
        lease.version().status().setValue(Lease.Status.Active);
        lease.leaseId().setValue("lease: " + dateFrom + " " + dateTo);
        lease.unit().set(unit);
        lease.type().setValue(Service.Type.residentialUnit);
        lease.createDate().setValue(asDate(dateFrom));
        lease.leaseFrom().setValue(asDate(dateFrom));
        lease.leaseTo().setValue(asDate(dateTo));
        lease.version().expectedMoveIn().setValue(asDate(dateFrom));
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        Tenant tenantInLease = EntityFactory.create(Tenant.class);
        tenantInLease.leaseV().set(lease.version());
        tenantInLease.customer().set(tenant);
        tenantInLease.orderInLease().setValue(1);
        tenantInLease.role().setValue(Tenant.Role.Applicant);
        lease.version().tenants().add(tenantInLease);
        Persistence.secureSave(lease);

        AptUnitOccupancySegment leased = AptUnitOccupancyManagerHelper.split(unit, asDate(dateFrom), new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {

            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.leased);
                segment.lease().set(lease);
            }
        });
        AptUnitOccupancyManagerHelper.split(leased, AptUnitOccupancyManagerHelper.addDay(asDate(dateTo)), new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
                segment.lease().set(null);
            }
        });
        Persistence.service().commit(); // for debugging's sake
        theMan.propagateLeaseActivationToTurnoverReport(lease);
        Persistence.service().commit(); // for debugging's sake
    }

    protected AptUnit unit(long pk) {
        String number = "unit-" + pk;
        EntityQueryCriteria<AptUnit> c = EntityQueryCriteria.create(AptUnit.class);
        c.add(PropertyCriterion.eq(c.proto().info().number(), number));
        AptUnit unit = Persistence.service().retrieve(c);
        if (unit == null) {
            unit = EntityFactory.create(AptUnit.class);
            unit.info().number().setValue(number);
            unit.belongsTo().set(building);
            unit.floorplan().set(floorplan);
            AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
            segment.status().setValue(Status.available);
            segment.dateFrom().setValue(OccupancyFacade.MIN_DATE);
            segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            unit._AptUnitOccupancySegment().add(segment);
            Persistence.service().merge(unit);
        }
        return unit;
    }

    protected void expect(String asOf, int turnovers) {
        Assert.assertEquals(turnovers, theMan.turnoversSinceBeginningOfTheMonth(asDate(asOf), building.getPrimaryKey()));
    }

    private LogicalDate asDate(String asDate) {
        return AptUnitOccupancyManagerTestBase.asDate(asDate);
    }
}
