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

import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantApplication;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.Tenant;
import com.propertyvista.portal.domain.ptapp.UnitSelection;

public class ApplicationCrudServiceImpl extends GenericCrudServiceDtoImpl<TenantApplication, ApplicationDTO> implements ApplicationCrudService {

    public ApplicationCrudServiceImpl() {
        super(TenantApplication.class, ApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieveDTO(TenantApplication tenantApplication, ApplicationDTO dto, boolean fromList) {
        if (fromList) {
            {
                EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().application(), tenantApplication.application()));
                UnitSelection unitSelection = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

                if (!unitSelection.selectedUnitId().isNull()) {
                    dto.selectedUnit().set(
                            PersistenceServicesFactory.getPersistenceService().retrieve(AptUnit.class, unitSelection.selectedUnitId().getValue()));
                }
            }

            {
                EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().application(), tenantApplication.application()));
                Tenant potentialTenantList = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

                if (potentialTenantList.tenants().size() > 0) {
                    PotentialTenantInfo potentialTenantInfo = potentialTenantList.tenants().get(0);
                    dto.primaryTenant().person().set(potentialTenantInfo.person());
                }
            }
        }
    }
}
