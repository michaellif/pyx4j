/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.onboarding.GetUsageRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.UsageRecordIO;
import com.propertyvista.onboarding.UsageReportResponseIO;
import com.propertyvista.onboarding.UsageType;
import com.propertyvista.server.jobs.TaskRunner;

public class GetUsageReportRequestHandler extends AbstractRequestHandler<GetUsageRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(GetUsageReportRequestHandler.class);

    public GetUsageReportRequestHandler() {
        super(GetUsageRequestIO.class);
    }

    @Override
    public ResponseIO execute(GetUsageRequestIO request) {
        log.info("User {} requested {}", new Object[] { request.onboardingAccountId().getValue(), "GetUsageReportRequest" });

        if (request.usageType().getValue() == UsageType.Equifax)
            return getEquifaxUsageResponse(request);
        else if (request.usageType().getValue() == UsageType.BuildingUnitCount)
            return getBuildingUnitCountResponse(request);
        else {
            UsageReportResponseIO response = EntityFactory.create(UsageReportResponseIO.class);
            response.success().setValue(Boolean.FALSE);
            response.errorMessage().setValue("Unknown usage type");

            return response;
        }
    }

    private UsageReportResponseIO getEquifaxUsageResponse(final GetUsageRequestIO request) {
        final UsageReportResponseIO response = EntityFactory.create(UsageReportResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        response.format().setValue(request.format().getValue());

        EntityQueryCriteria<Pmc> pmcCret = EntityQueryCriteria.create(Pmc.class);
        pmcCret.add(PropertyCriterion.eq(pmcCret.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        Pmc pmc = Persistence.service().retrieve(pmcCret);
        if (pmc == null) {
            response.success().setValue(Boolean.FALSE);
            response.errorMessage().setValue("PMC not found");
            return response;
        }

        final UsageRecordIO record = EntityFactory.create(UsageRecordIO.class);
        record.from().setValue(request.from().getValue());
        record.to().setValue(request.to().getValue());
        record.usageType().setValue(request.usageType().getValue());

        TaskRunner.runInTargetNamespace(pmc.namespace().getValue(), new Callable<Void>() {
            @Override
            public Void call() {
                EntityQueryCriteria<CustomerCreditCheck> criteria = EntityQueryCriteria.create(CustomerCreditCheck.class);
                criteria.add(PropertyCriterion.ge(criteria.proto().creditCheckDate(), request.from().getValue()));
                criteria.add(PropertyCriterion.le(criteria.proto().creditCheckDate(), request.to().getValue()));
                record.value().setValue(Persistence.service().count(criteria));
                return null;
            }
        });

        response.records().add(record);

        return response;
    }

    private UsageReportResponseIO getBuildingUnitCountResponse(final GetUsageRequestIO request) {
        final UsageReportResponseIO response = EntityFactory.create(UsageReportResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        response.format().setValue(request.format().getValue());

        EntityQueryCriteria<Pmc> pmcCret = EntityQueryCriteria.create(Pmc.class);
        pmcCret.add(PropertyCriterion.eq(pmcCret.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        Pmc pmc = Persistence.service().retrieve(pmcCret);
        if (pmc == null) {
            response.success().setValue(Boolean.FALSE);
            response.errorMessage().setValue("PMC not found");
            return response;
        }

        TaskRunner.runInTargetNamespace(pmc.namespace().getValue(), new Callable<Void>() {
            @Override
            public Void call() {
                EntityQueryCriteria<Building> buildingCret = EntityQueryCriteria.create(Building.class);
                ICursorIterator<Building> buildings = Persistence.service().query(null, buildingCret, AttachLevel.Attached);
                while (buildings.hasNext()) {
                    Building building = buildings.next();
                    UsageRecordIO record = EntityFactory.create(UsageRecordIO.class);

                    EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));

                    record.text().setValue(building.propertyCode().getValue());
                    record.value().setValue(Persistence.service().count(criteria));
                    record.usageType().setValue(request.usageType().getValue());

                    response.records().add(record);
                }
                return null;
            }
        });

        return response;
    }
}
