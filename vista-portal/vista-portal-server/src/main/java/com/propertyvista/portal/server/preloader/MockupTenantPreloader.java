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

import java.util.List;
import java.util.Random;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrear;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupTenant;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;
import com.propertyvista.domain.property.asset.unit.AptUnit;

/**
 * Generates and preloads mockup tenants for the Arrears Gadget Demos
 * 
 * @author ArtyomB
 * 
 */
public class MockupTenantPreloader extends BaseVistaDevDataPreloader {
    private static final long ONE_DAY = 1000l * 60l * 60l * 24l;

    private static final long MAX_LEASE = 24l * 60l * 60l * 1000l * 24l * 30l;

    private static final double MAX_ARREAR = 3000d;

    private static final double MAX_ARBALANCE = 10000d;

    private static final double MAX_PREPAYMENTS = 3000d;

    private static final Random RND = new Random(9001);

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(MockupArrear.class) + "; " + deleteAll(MockupTenant.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {
        int counter = 0;
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        List<AptUnit> units = Persistence.service().query(criteria);
        final LogicalDate startDate = new LogicalDate();
        startDate.setYear(106);
        startDate.setMonth(0);
        startDate.setDate(1);
        final LogicalDate endDate = new LogicalDate();

        for (AptUnit unit : units) {
            LogicalDate movein = new LogicalDate(startDate.getTime() + Math.abs(RND.nextLong()) % MAX_LEASE);

            while (movein.before(endDate)) {
                LogicalDate moveout = new LogicalDate(movein.getTime() + Math.abs(RND.nextLong()) % MAX_LEASE);

                MockupTenant tenant = EntityFactory.create(MockupTenant.class);
                tenant.belongsTo().set(unit);
                tenant.moveIn().setValue(movein);
                tenant.moveOut().setValue(moveout);
                tenant.firstName().setValue(RandomUtil.randomFirstName());
                tenant.lastName().setValue(RandomUtil.randomLastName());
                tenant.arBalance().setValue(RandomUtil.randomDouble(MAX_ARBALANCE));
                tenant.prepayments().setValue(RandomUtil.randomDouble(MAX_PREPAYMENTS));
                Persistence.service().persist(tenant);
                ++counter;

                // create mockup arrears history
                LogicalDate currentMonth = new LogicalDate(AnalysisResolution.Month.intervalStart(movein.getTime()));
                while (currentMonth.before(moveout)) {
                    MockupArrear arrear = EntityFactory.create(MockupArrear.class);
                    arrear.month().setValue(currentMonth);
                    arrear.belongsTo().set(tenant);
                    arrear.amount().setValue((RND.nextInt() % 10 < 7) ? RandomUtil.randomDouble(MAX_ARREAR) : 0d);
                    Persistence.service().persist(arrear);
                    currentMonth = new LogicalDate(AnalysisResolution.Month.addTo(currentMonth));
                }
                movein = new LogicalDate(moveout.getTime() + ONE_DAY);
            }

        }

        return "Created " + counter + " mockup tennants for Arrears Gadget";
    }
}
