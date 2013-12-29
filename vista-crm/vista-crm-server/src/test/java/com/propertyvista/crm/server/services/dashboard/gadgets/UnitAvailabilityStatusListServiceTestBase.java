/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.math.BigDecimal;

import junit.framework.Assert;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.crm.rpc.dto.gadgets.UnitAvailabilityStatusDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitAvailabilityStatusListService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.common.AsOfDateCriterion;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class UnitAvailabilityStatusListServiceTestBase extends VistaDBTestBase {

    private final UnitAvailabilityStatusListService service;

    public UnitAvailabilityStatusListServiceTestBase(UnitAvailabilityStatusListService service) {
        this.service = service;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestLifecycle.testSession(new UserVisit(new Key(-101), "Neo"), VistaCrmBehavior.Occupancy, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);

        status.statusFrom().setValue(new LogicalDate(DateUtils.detectDateformat("10-25-2012")));
        status.statusUntil().setValue(new LogicalDate(DateUtils.detectDateformat("12-31-2012")));

        status.vacancyStatus().setValue(Vacancy.Vacant);
        status.vacantSince().setValue(new LogicalDate(DateUtils.detectDateformat("10-25-2012")));
        status.marketRent().setValue(new BigDecimal("3000.00"));
        Persistence.service().persist(status);

    }

    @Override
    protected void tearDown() throws Exception {
        try {
            Persistence.service().commit();
        } finally {
            TestLifecycle.tearDown();
            super.tearDown();
        }
    }

    public void testDaysVacantCalculation() {
        {
            EntityListCriteria<UnitAvailabilityStatusDTO> criteria = EntityListCriteria.create(UnitAvailabilityStatusDTO.class);
            criteria.add(new AsOfDateCriterion(new LogicalDate(DateUtils.detectDateformat("10-25-2012"))));
            final UnitAvailabilityStatusDTO status = invokeUnitAvailabilityStatusListService(criteria);
            Assert.assertEquals(new Integer(1), status.daysVacant().getValue());
        }

        {
            EntityListCriteria<UnitAvailabilityStatusDTO> criteria = EntityListCriteria.create(UnitAvailabilityStatusDTO.class);
            criteria.add(new AsOfDateCriterion(new LogicalDate(DateUtils.detectDateformat("10-30-2012"))));
            UnitAvailabilityStatusDTO status = invokeUnitAvailabilityStatusListService(criteria);
            Assert.assertEquals(new Integer(6), status.daysVacant().getValue());
        }

        {
            EntityListCriteria<UnitAvailabilityStatusDTO> criteria = EntityListCriteria.create(UnitAvailabilityStatusDTO.class);
            criteria.add(new AsOfDateCriterion(new LogicalDate(DateUtils.detectDateformat("11-01-2012"))));
            UnitAvailabilityStatusDTO status = invokeUnitAvailabilityStatusListService(criteria);
            Assert.assertEquals(new Integer(8), status.daysVacant().getValue());
        }
    }

    public void testRevenueLostCalculation() {
        {
            EntityListCriteria<UnitAvailabilityStatusDTO> criteria = EntityListCriteria.create(UnitAvailabilityStatusDTO.class);
            criteria.add(new AsOfDateCriterion(new LogicalDate(DateUtils.detectDateformat("10-25-2012"))));
            final UnitAvailabilityStatusDTO status = invokeUnitAvailabilityStatusListService(criteria);
            Assert.assertEquals(new BigDecimal("100.00"), status.revenueLost().getValue());
        }

        {
            EntityListCriteria<UnitAvailabilityStatusDTO> criteria = EntityListCriteria.create(UnitAvailabilityStatusDTO.class);
            criteria.add(new AsOfDateCriterion(new LogicalDate(DateUtils.detectDateformat("10-30-2012"))));
            UnitAvailabilityStatusDTO status = invokeUnitAvailabilityStatusListService(criteria);
            Assert.assertEquals(new BigDecimal("600.00"), status.revenueLost().getValue());
        }

        {
            EntityListCriteria<UnitAvailabilityStatusDTO> criteria = EntityListCriteria.create(UnitAvailabilityStatusDTO.class);
            criteria.add(new AsOfDateCriterion(new LogicalDate(DateUtils.detectDateformat("11-01-2012"))));
            UnitAvailabilityStatusDTO status = invokeUnitAvailabilityStatusListService(criteria);
            Assert.assertEquals(new BigDecimal("800.00"), status.revenueLost().getValue());
        }
    }

    private UnitAvailabilityStatusDTO invokeUnitAvailabilityStatusListService(EntityListCriteria<UnitAvailabilityStatusDTO> criteria) {
        final UnitAvailabilityStatusDTO[] status = new UnitAvailabilityStatusDTO[1];
        service.list(new AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>>() {

            @Override
            public void onSuccess(EntitySearchResult<UnitAvailabilityStatusDTO> result) {
                status[0] = result.getData().get(0);
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        }, criteria);
        return status[0];
    }

}
