/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrear;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupTenant;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

/**
 * Generates and preloads mockup tenants for the Arrears Gadget Demos.
 * 
 * @author artyom
 * 
 */
public class MockupTenantPreloader extends AbstractMockupPreloader {

    private static final Random RND = new Random(9001);

    //@formatter:off
    private static final long ONE_DAY = 1000l * 60l * 60l * 24l;
    private static final long MAX_LEASE = 24l * 60l * 60l * 1000l * 24l * 30l;
    private static final double MAX_ARREAR = 3000d;
    private static final double MAX_ARBALANCE = 10000d;
    
    private static final double HAS_ARREAR_CHANCE = 0.2;
    private static final double HAS_PREPAYMENT_CHANCE = 0.1;
    private static final double MAX_PREPAYMENTS = 2000d;
    //@formatter:on

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(MockupArrear.class, MockupTenant.class);
    }

    @Override
    public String createMockup() {
        int tenantCounter = 0;
        int statusCounter = 0;

        List<Building> allBuildings = Persistence.service().query(EntityQueryCriteria.create(Building.class));
        for (Building building : allBuildings) {
            EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
            List<AptUnit> units = Persistence.service().query(criteria);

            final LogicalDate startDate = new LogicalDate();
            startDate.setYear(106);
            startDate.setMonth(0);
            startDate.setDate(1);
            final LogicalDate endDate = new LogicalDate();

            List<MockupTenant> tenants = new ArrayList<MockupTenant>();
            List<MockupArrear> arrears = new ArrayList<MockupArrear>();
            int maxArraySize = 1000;

            for (AptUnit unit : units) {
                LogicalDate movein = new LogicalDate(startDate.getTime() + Math.abs(RND.nextLong()) % MAX_LEASE);

                // You don't need to load the building again
                unit.belongsTo().set(building);

                while (movein.before(endDate)) {
                    LogicalDate moveout = new LogicalDate(movein.getTime() + Math.abs(RND.nextLong()) % MAX_LEASE);

                    MockupTenant tenant = EntityFactory.create(MockupTenant.class);
                    tenant.belongsTo().setPrimaryKey(unit.getPrimaryKey());
                    tenant.moveIn().setValue(movein);
                    tenant.moveOut().setValue(moveout);
                    tenant.firstName().setValue(RandomUtil.randomFirstName());
                    tenant.lastName().setValue(RandomUtil.randomLastName());
                    tenants.add(tenant);
                    ++tenantCounter;

                    // create mockup arrears history
                    LogicalDate currentMonth = new LogicalDate(AnalysisResolution.Month.intervalStart(movein.getTime()));
                    while (currentMonth.before(moveout)) {
                        MockupArrear arrear = EntityFactory.create(MockupArrear.class);

                        // FIXME this is doesn't feel right! we cannot store values that belong to properties of other entities that are prone to updates (i.e. tenant's name, building property code, unit number etc.)                    
                        arrear.belongsTo().setPrimaryKey(tenant.getPrimaryKey());
                        arrear.firstName().setValue(tenant.firstName().getValue());
                        arrear.lastName().setValue(tenant.lastName().getValue());

                        arrear.unit().setPrimaryKey(unit.getPrimaryKey());
                        arrear.unitNumber().setValue(unit.info().number().getValue());

                        arrear.building().setPrimaryKey(unit.belongsTo().getPrimaryKey());
                        arrear.propertyCode().setValue(unit.belongsTo().propertyCode().getValue());

                        arrear.monthAgo().setValue(randomArrear());
                        arrear.twoMonthsAgo().setValue(randomArrear());
                        arrear.threeMonthsAgo().setValue(randomArrear());
                        arrear.overFourMonthsAgo().setValue(randomArrear());
                        arrear.arBalance().setValue(randomARBalance());
                        arrear.prepayments().setValue(randomPrepayments());
                        arrear.totalBalance().setValue(arrear.arBalance().getValue() - arrear.prepayments().getValue());

                        arrear.statusTimestamp().setValue(currentMonth);
                        arrears.add(arrear);
                        ++statusCounter;

                        currentMonth = new LogicalDate(AnalysisResolution.Month.addTo(currentMonth));
                    }
                    movein = new LogicalDate(moveout.getTime() + ONE_DAY);
                }

                if (arrears.size() > maxArraySize) {
                    persistArray(tenants);
                    persistArray(arrears);
                    tenants.clear();
                    arrears.clear();
                }
            }
            persistArray(tenants);
            persistArray(arrears);

        }

        return "Created " + tenantCounter + " mockup tennants and " + statusCounter + " arrear statuses for Arrears Gadget";
    }

    private double randomArrear() {
        return (RND.nextDouble() <= HAS_ARREAR_CHANCE) ? RND.nextDouble() * MAX_ARREAR : 0d;
    }

    private double randomARBalance() {
        return RND.nextDouble() * MAX_ARBALANCE;
    }

    private double randomPrepayments() {
        return (RND.nextDouble() <= HAS_PREPAYMENT_CHANCE) ? RND.nextDouble() * MAX_PREPAYMENTS : 0d;
    }

}
