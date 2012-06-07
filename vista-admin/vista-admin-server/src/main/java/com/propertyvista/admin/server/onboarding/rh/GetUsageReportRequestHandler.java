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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.onboarding.GetUsageRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.UsageRecordIO;
import com.propertyvista.onboarding.UsageReportResponseIO;
import com.propertyvista.onboarding.UsageType;

public class GetUsageReportRequestHandler extends AbstractRequestHandler<GetUsageRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(GetUsageReportRequestHandler.class);

    public GetUsageReportRequestHandler() {
        super(GetUsageRequestIO.class);
    }

    @Override
    public ResponseIO execute(GetUsageRequestIO request) {

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

    private UsageReportResponseIO getEquifaxUsageResponse(GetUsageRequestIO request) {
        UsageReportResponseIO response = EntityFactory.create(UsageReportResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        response.format().setValue(request.format().getValue());

        UsageRecordIO record = EntityFactory.create(UsageRecordIO.class);
        record.from().setValue(request.from().getValue());
        record.to().setValue(request.to().getValue());
        record.usageType().setValue(request.usageType().getValue());

        if (ApplicationMode.isDevelopment())
            record.value().setValue(55);
        else
            record.value().setValue(0);

        response.records().add(record);

        return response;
    }

    private UsageReportResponseIO getBuildingUnitCountResponse(GetUsageRequestIO request) {
        UsageReportResponseIO response = EntityFactory.create(UsageReportResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        response.format().setValue(request.format().getValue());

        EntityQueryCriteria<Pmc> pmcCret = EntityQueryCriteria.create(Pmc.class);
        pmcCret.add(PropertyCriterion.eq(pmcCret.proto().onboardingAccountId(), request.onboardingAccountId().getValue()));
        List<Pmc> pmcs = Persistence.service().query(pmcCret);

        if (pmcs.size() == 0) {
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        final String cuurentNamespace = NamespaceManager.getNamespace();
        NamespaceManager.setNamespace(pmcs.get(0).namespace().getValue());

        try {
            EntityQueryCriteria<Building> buildingCret = EntityQueryCriteria.create(Building.class);
            List<Building> buildings = Persistence.service().query(buildingCret);

            for (Building building : buildings) {
                UsageRecordIO record = EntityFactory.create(UsageRecordIO.class);

                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), building));

                record.text().setValue(building.propertyCode().getValue());
                record.value().setValue(Persistence.service().count(criteria));
                record.usageType().setValue(request.usageType().getValue());

                response.records().add(record);
            }
        } finally {
            NamespaceManager.setNamespace(cuurentNamespace);
        }

        return response;
    }
}
