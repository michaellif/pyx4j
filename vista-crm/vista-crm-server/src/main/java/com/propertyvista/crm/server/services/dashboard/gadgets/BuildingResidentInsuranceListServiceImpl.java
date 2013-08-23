/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.InMemeoryListService;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.dto.gadgets.BuildingResidentInsuranceCoverageDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.BuildingResidentInsuranceListService;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;

public class BuildingResidentInsuranceListServiceImpl implements BuildingResidentInsuranceListService {

    @Override
    public void list(AsyncCallback<EntitySearchResult<BuildingResidentInsuranceCoverageDTO>> callback,
            EntityListCriteria<BuildingResidentInsuranceCoverageDTO> criteria) {

        Vector<BuildingResidentInsuranceCoverageDTO> values = new Vector<BuildingResidentInsuranceCoverageDTO>();
        // extract buildings filter:

        BuildingResidentInsuranceCoverageDTO proto = EntityFactory.getEntityPrototype(BuildingResidentInsuranceCoverageDTO.class);
        Serializable buildingsFilter = null;
        if (criteria.getFilters() != null) {
            for (Iterator<Criterion> criteriaIterator = criteria.getFilters().iterator(); criteriaIterator.hasNext();) {
                Criterion criterion = criteriaIterator.next();
                if (criterion instanceof PropertyCriterion) {
                    PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;
                    if (propertyCriterion.getPropertyPath().equals(proto.buildingFilter().getPath().toString())) {
                        criteriaIterator.remove();
                        buildingsFilter = propertyCriterion.getValue();
                    }
                }
            }
        }

        EntityQueryCriteria<Building> buildingsCriteria = EntityQueryCriteria.create(Building.class);
        if (buildingsFilter != null) {
            buildingsCriteria.in(buildingsCriteria.proto().id(), (Vector<Building>) buildingsFilter);
        }
        ICursorIterator<Building> buildingIterator = Persistence.secureQuery(null, buildingsCriteria, AttachLevel.Attached);

        while (buildingIterator.hasNext()) {
            Building b = buildingIterator.next();

            BuildingResidentInsuranceCoverageDTO dto = EntityFactory.create(BuildingResidentInsuranceCoverageDTO.class);
            dto.building().setValue(b.propertyCode().getValue());
            Persistence.service().retrieve(b.complex());
            dto.complex().setValue(b.complex().name().getValue());

            EntityQueryCriteria<AptUnit> unitsCriteria = EntityQueryCriteria.create(AptUnit.class);
            unitsCriteria.eq(unitsCriteria.proto().building(), b);
            dto.units().setValue(Persistence.service().count(unitsCriteria));

            EntityQueryCriteria<InsuranceCertificate> insuranceCriteria = EntityQueryCriteria.create(InsuranceCertificate.class);
            insuranceCriteria.eq(insuranceCriteria.proto().tenant().lease().unit().building(), b);
            LogicalDate now = new LogicalDate();
            insuranceCriteria.or().left(PropertyCriterion.isNull(insuranceCriteria.proto().expiryDate()))
                    .right(PropertyCriterion.ge(insuranceCriteria.proto().expiryDate(), now));
            insuranceCriteria.le(insuranceCriteria.proto().inceptionDate(), now);

            TenantInsurancePolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(b, TenantInsurancePolicy.class);
            if (policy.requireMinimumLiability().isBooleanTrue()) {
                insuranceCriteria.ge(insuranceCriteria.proto().liabilityCoverage(), policy.minimumRequiredLiability().getValue());
            }

            dto.unitsWithInsuranceCount().setValue(Persistence.service().count(insuranceCriteria));

            if (dto.units().getValue() > 0) {
                dto.unitsWithInsuranceShare().setValue(dto.unitsWithInsuranceCount().getValue() / (double) dto.units().getValue());
            }
            values.add(dto);
        }
        IOUtils.closeQuietly(buildingIterator);

        new InMemeoryListService<BuildingResidentInsuranceCoverageDTO>(values).list(callback, criteria);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error("not supported");
    }

}
