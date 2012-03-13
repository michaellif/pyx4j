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

import org.junit.Assert;
import org.junit.Before;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoverStats;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.server.common.util.LeaseLifecycleSim;

public class UnitTurnoverAnalysisManagerTestBase {

    private Building building;

    private Floorplan floorplan;

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
        Persistence.service().delete(EntityQueryCriteria.create(Lease.class));
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

    protected void lease(AptUnit unit, String dateFrom, String dateTo) {
        Tenant tenant = EntityFactory.create(Tenant.class);
        tenant.type().setValue(Tenant.Type.person);
        TenantUser user = EntityFactory.create(TenantUser.class);
        user.name().setValue("tenant " + dateFrom);
        Persistence.service().merge(user);
        tenant.user().set(user);
        Persistence.service().merge(tenant);

        LeaseLifecycleSim sim = new LeaseLifecycleSim();
        Lease lease = sim.newLease(asDate(dateFrom), "lease: " + dateFrom + " " + dateTo, unit, asDate(dateFrom), asDate(dateTo), asDate(dateFrom),
                PaymentFrequency.Monthly, tenant);
        sim.createApplication(lease.getPrimaryKey(), lease.leaseFrom().getValue());
        sim.approveApplication(lease.getPrimaryKey(), lease.leaseFrom().getValue());
        sim.activate(lease.getPrimaryKey(), lease.leaseFrom().getValue());
        sim.notice(lease.getPrimaryKey(), lease.leaseTo().getValue(), lease.leaseTo().getValue());
        sim.complete(lease.getPrimaryKey(), lease.leaseTo().getValue());
    }

    protected AptUnit unit(long pk) {
        String number = "unit-" + pk;
        EntityQueryCriteria<AptUnit> c = EntityQueryCriteria.create(AptUnit.class);
        c.add(PropertyCriterion.eq(c.proto().info().number(), number));
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, new Key(pk));
        if (unit == null) {
            unit = EntityFactory.create(AptUnit.class);
            unit.info().number().setValue(number);
            unit.belongsTo().set(building);
            unit.floorplan().set(floorplan);
            AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
            segment.status().setValue(Status.available);
            segment.dateFrom().setValue(AptUnitOccupancyManagerHelper.MIN_DATE);
            segment.dateTo().setValue(AptUnitOccupancyManagerHelper.MAX_DATE);
            unit._AptUnitOccupancySegment().add(segment);
            Persistence.service().merge(unit);
        }
        return unit;
    }

    protected void recalcTurnovers(String date) {
        new UnitTurnoverAnalysisManagerImpl().recalculateTurnovers(building.getPrimaryKey(), asDate(date));
    }

    protected void expect(String onDate, int turnovers) {
        EntityQueryCriteria<UnitTurnoverStats> criteria = EntityQueryCriteria.create(UnitTurnoverStats.class);
        criteria.add(PropertyCriterion.ge(criteria.proto().updatedOn(), asDate(onDate)));
        criteria.add(PropertyCriterion.le(criteria.proto().updatedOn(), asDate(onDate)));
        criteria.desc(criteria.proto().updatedOn());

        UnitTurnoverStats stats = Persistence.service().retrieve(criteria);
        Assert.assertNotNull(stats);
        Assert.assertEquals(new Integer(turnovers), stats.turnovers().getValue());
    }

    private LogicalDate asDate(String asDate) {
        return AptUnitOccupancyManagerTestBase.asDate(asDate);
    }
}
