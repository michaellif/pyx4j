/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.portal.domain.ptapp.UnitSelection;

public class LeaseCrudServiceImpl extends GenericCrudServiceDtoImpl<Lease, LeaseDTO> implements LeaseCrudService {

    public LeaseCrudServiceImpl() {
        super(Lease.class, LeaseDTO.class);
    }

    @Override
    protected void enhanceRetrieveDTO(Lease in, LeaseDTO dto, boolean fromList) {
        if (!fromList) {
            // fill selected building & unit:
            if (in.unit().isEmpty()) {
                EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().lease(), in));
                UnitSelection unitSelection = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
                if (!unitSelection.selectedUnitId().isNull()) {
                    dto.unit().set(PersistenceServicesFactory.getPersistenceService().retrieve(AptUnit.class, unitSelection.selectedUnitId().getValue()));
                }
            }
            // fill transient data: 
            dto.selectedBuilding().set(PersistenceServicesFactory.getPersistenceService().retrieve(Building.class, dto.unit().belongsTo().getPrimaryKey()));

            dto.tenants().clear();
            EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), in));
            dto.tenants().addAll(PersistenceServicesFactory.getPersistenceService().query(criteria));
        }
    }

    @Override
    protected void enhanceSaveDTO(Lease dbo, LeaseDTO dto) {
        // set currently selected unit:
        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), dbo));
        UnitSelection unitSelection = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        unitSelection.selectedUnitId().setValue(dto.unit().getPrimaryKey());
        // update Tenants:
        for (TenantInLease tenant : dto.tenants()) {
            tenant.lease().set(dbo);
        }
    }
}
