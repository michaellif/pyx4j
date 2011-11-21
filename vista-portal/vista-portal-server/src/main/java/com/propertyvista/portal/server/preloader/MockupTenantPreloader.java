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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.company.AssignedBuilding;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.dashboard.gadgets.arrears.Arrears;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsSummary;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState.LegalStatus;
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
    private static final List<LegalStatus> LEGAL_STATUSES = Arrays.asList(LegalStatus.values());
    
    private static final long ONE_DAY = 1000l * 60l * 60l * 24l;
    private static final long MIN_LEASE = 24l * 60l * 60l * 1000l * 365l;
    private static final long MAX_LEASE = 24l * 60l * 60l * 1000l * 365l * 3l;
    private static final double MAX_ARREAR = 3000d;
    private static final double MAX_ARBALANCE = 10000d;
    
    private static final double HAS_ARREAR_CHANCE = 0.2;
    private static final double HAS_PREPAYMENT_CHANCE = 0.1;
    private static final double MAX_PREPAYMENTS = 2000d;
    
    private static final double LMR_UNIT_RENT_DIFFERENCE_CHANCE = 0.1;

    private static final double LEGAL_STATUS_CHANCE = 0.5;
    //@formatter:on

    private static final List<String> REGIONS = Arrays.asList("GTA", "West", "East");

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(MockupArrearsState.class, MockupTenant.class, ArrearsSummary.class);
    }

    @Override
    public String createMockup() {
        final int maxArraySize = 2000;
        List<MockupArrearsState> arrears = new ArrayList<MockupArrearsState>();
        List<MockupTenant> tenants = new ArrayList<MockupTenant>();
        List<ArrearsSummary> buildingArrears = new ArrayList<ArrearsSummary>();

        for (Building building : Persistence.service().query(EntityQueryCriteria.create(Building.class))) {
            Persistence.service().retrieve(building.complex());
            EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));
            List<AptUnit> units = Persistence.service().query(criteria);

            // TODO add owner
//            EntityQueryCriteria<Owner> ownerCriteria = new EntityQueryCriteria<Owner>(Owner.class);
//            ownerCriteria.add(PropertyCriterion.eq(ownerCriteria.proto().building(), building));
//            List<Owner> owners = Persistence.service().query(ownerCriteria); 

            EntityQueryCriteria<Portfolio> portfolioCriteria = new EntityQueryCriteria<Portfolio>(Portfolio.class);
            Iterator<Portfolio> i = Persistence.service().query(portfolioCriteria).iterator();
            Portfolio portfolio = null;
            while (i.hasNext()) {
                Portfolio tmp = i.next();
                for (AssignedBuilding assBuilding : tmp.buildings()) {
                    if (assBuilding.building().equals(building)) {
                        portfolio = tmp;
                        break;
                    }
                }
                if (portfolio != null) {
                    break;
                }
            }

            final LogicalDate startDate = new LogicalDate();
            startDate.setYear(108);
            startDate.setMonth(0);
            startDate.setDate(1);
            final LogicalDate endDate = new LogicalDate();

            List<MockupTenant> buildingTenants = new ArrayList<MockupTenant>(units.size() * 3);
            for (AptUnit unit : units) {
                LogicalDate movein = new LogicalDate(startDate.getTime() + Math.abs(RND.nextLong()) % MAX_LEASE);

                while (movein.before(endDate)) {
                    LogicalDate moveout = new LogicalDate(movein.getTime() + Math.max(MIN_LEASE, RND.nextLong() % MAX_LEASE));

                    MockupTenant tenant = EntityFactory.create(MockupTenant.class);
                    tenant.belongsTo().set(unit);
                    tenant.unitNumber().setValue(unit.info().number().getValue());
                    tenant.moveIn().setValue(movein);
                    tenant.moveOut().setValue(moveout);
                    tenant.firstName().setValue(RandomUtil.randomFirstName());
                    tenant.lastName().setValue(RandomUtil.randomLastName());
                    buildingTenants.add(tenant);

                    movein = new LogicalDate(moveout.getTime() + ONE_DAY);
                }
            } // tenants
            tenants.addAll(buildingTenants);

            HashMap<LogicalDate, ArrearsSummary> monthToBuildingArrearsMap = new HashMap<LogicalDate, ArrearsSummary>();

            // create mockup arrears states for these tenants
            for (MockupTenant tenant : buildingTenants) {
                LogicalDate currentMonth = new LogicalDate(AnalysisResolution.Month.intervalStart(tenant.moveIn().getValue().getTime()));
                LogicalDate lastMonth = tenant.moveOut().getValue().before(endDate) ? tenant.moveOut().getValue() : endDate;

                while (currentMonth.before(lastMonth)) {
                    MockupArrearsState arrear = EntityFactory.create(MockupArrearsState.class);

                    arrear.statusTimestamp().setValue(currentMonth);

                    // FIXME this is doesn't feel right! we cannot store values that belong to properties of other entities that are prone to updates (i.e. tenant's name, building property code, unit number etc.)                    
                    arrear.belongsTo().set(tenant);
                    arrear.firstName().setValue(tenant.firstName().getValue());
                    arrear.lastName().setValue(tenant.lastName().getValue());

                    arrear.unit().setPrimaryKey(tenant.belongsTo().getPrimaryKey());

                    arrear.building().set(building);
                    arrear.propertyCode().setValue(building.propertyCode().getValue());
                    arrear.complexName().setValue(building.complex().name().getValue());

                    // TODO set owner
                    // arrear.common().owner().set(null);
                    arrear.common().propertyManger().set(building.propertyManager());
                    arrear.common().portfolio().set(portfolio);
                    // TODO set normal value form somewhere
                    arrear.common().region().setValue(randomRegion());

                    arrear.streetNumber().setValue(building.info().address().streetNumber().getValue());
                    arrear.streetName().setValue(building.info().address().streetName().getValue());
                    arrear.streetType().setValue(building.info().address().streetType().getValue());
                    arrear.unitNumber().setValue(tenant.unitNumber().getValue());
                    arrear.city().setValue(building.info().address().city().getValue());

                    // TODO set VS setValue? what to use?
                    arrear.province().setValue(building.info().address().province().getValue());
                    arrear.country().setValue(building.info().address().country().getValue());

                    // financial stuff
                    generateRandomArrears(arrear.rentArrears());
                    generateRandomArrears(arrear.parkingArrears());
                    generateRandomArrears(arrear.otherArrears());
                    sumArrears(arrear, Arrays.asList(arrear.rentArrears().getPath(), arrear.parkingArrears().getPath(), arrear.otherArrears().getPath()));
                    arrear.lmrUnitRentDifference().setValue(randomLmrUnitRentDifference());
                    if (arrear.totalArrears().totalBalance().getValue() > 0.0) {
                        arrear.legalStatus().setValue(randomLegalStatus());
                    }

                    arrears.add(arrear);

                    if (!monthToBuildingArrearsMap.containsKey(currentMonth)) {
                        ArrearsSummary summary = EntityFactory.create(ArrearsSummary.class);
                        summary.statusTimestamp().setValue(currentMonth);
                        summary.belongsTo().set(building);

                        summary.thisMonth().setValue(0.0);
                        summary.monthAgo().setValue(0.0);
                        summary.twoMonthsAgo().setValue(0.0);
                        summary.threeMonthsAgo().setValue(0.0);
                        summary.overFourMonthsAgo().setValue(0.0);
                        summary.totalBalance().setValue(0.0);
                        summary.arBalance().setValue(0.0);
                        monthToBuildingArrearsMap.put(currentMonth, summary);
                    }
                    ArrearsSummary summary = monthToBuildingArrearsMap.get(currentMonth);
                    summary.thisMonth().setValue(summary.thisMonth().getValue() + arrear.totalArrears().thisMonth().getValue());
                    summary.monthAgo().setValue(summary.monthAgo().getValue() + arrear.totalArrears().monthAgo().getValue());
                    summary.twoMonthsAgo().setValue(summary.twoMonthsAgo().getValue() + arrear.totalArrears().twoMonthsAgo().getValue());
                    summary.threeMonthsAgo().setValue(summary.threeMonthsAgo().getValue() + arrear.totalArrears().threeMonthsAgo().getValue());
                    summary.overFourMonthsAgo().setValue(summary.overFourMonthsAgo().getValue() + arrear.totalArrears().overFourMonthsAgo().getValue());
                    summary.totalBalance().setValue(summary.totalBalance().getValue() + arrear.totalArrears().totalBalance().getValue());
                    summary.arBalance().setValue(summary.arBalance().getValue() + arrear.totalArrears().arBalance().getValue());

                    currentMonth = new LogicalDate(AnalysisResolution.Month.addTo(currentMonth));
                }
            } // arrears creation
            buildingArrears.addAll(monthToBuildingArrearsMap.values());
        } // buildings iteration
        final int tenantsSize = tenants.size();
        for (int i = 0; i < tenantsSize;) {
            persistArrayWithId(tenants.subList(i, Math.min(tenantsSize, i += maxArraySize)));
        }
        final int arrearsSize = arrears.size();
        for (int i = 0; i < arrearsSize;) {
            persistArray(arrears.subList(i, Math.min(arrears.size(), i += maxArraySize)));
        }
        persistArray(buildingArrears);
        return "Created " + tenantsSize + " mockup tennants, " + arrearsSize + " unit arrear statuses, " + buildingArrears.size()
                + " building arrear statuses for Arrears Gadget";
    }

    private Double randomLmrUnitRentDifference() {
        return RND.nextDouble() < LMR_UNIT_RENT_DIFFERENCE_CHANCE ? RND.nextDouble() * MAX_ARREAR * 0.5 : null;
    }

    private LegalStatus randomLegalStatus() {
        return RND.nextDouble() < LEGAL_STATUS_CHANCE ? RandomUtil.randomChoice(RND, LEGAL_STATUSES, 1).get(0) : LegalStatus.Clean;
    }

    private static void sumArrears(MockupArrearsState arrearsCompilation, List<Path> arrearsCategory) {
        for (String member : arrearsCompilation.totalArrears().getEntityMeta().getMemberNames()) {
            arrearsCompilation.totalArrears().setMemberValue(member, 0.0);
        }

        for (Path arrearsPath : arrearsCategory) {
            for (String member : arrearsCompilation.totalArrears().getEntityMeta().getMemberNames()) {
                Double value = (Double) arrearsCompilation.totalArrears().getMemberValue(member);
                Double otherValue = (Double) ((Arrears) arrearsCompilation.getMember(arrearsPath)).getMemberValue(member);
                arrearsCompilation.totalArrears().setMemberValue(member, value + otherValue);
            }
        }

    }

    private static void generateRandomArrears(Arrears arrears) {
        arrears.thisMonth().setValue(randomArrear());
        arrears.monthAgo().setValue(randomArrear());
        arrears.twoMonthsAgo().setValue(randomArrear());
        arrears.threeMonthsAgo().setValue(randomArrear());
        arrears.overFourMonthsAgo().setValue(randomArrear());
        arrears.arBalance().setValue(randomARBalance());
        arrears.prepayments().setValue(randomPrepayments());
        arrears.totalBalance().setValue(arrears.arBalance().getValue() - arrears.prepayments().getValue());
    }

    private static String randomRegion() {
        return RandomUtil.randomChoice(RND, REGIONS, 1).get(0);
    }

    private static double randomArrear() {
        return (RND.nextDouble() <= HAS_ARREAR_CHANCE) ? RND.nextDouble() * MAX_ARREAR : 0d;
    }

    private static double randomARBalance() {
        return RND.nextDouble() * MAX_ARBALANCE;
    }

    private static double randomPrepayments() {
        return (RND.nextDouble() <= HAS_PREPAYMENT_CHANCE) ? RND.nextDouble() * MAX_PREPAYMENTS : 0d;
    }

}
