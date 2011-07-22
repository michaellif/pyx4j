/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.common.domain.tenant.Tenant;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantApplication;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.dto.PetsDTO;

@Transient
public interface ApplicationDTO extends TenantApplication {

    Building selectedBuilding();

    AptUnit selectedUnit();

    Tenant primaryTenant();

//  Note: While show ApplicationDTO - make 2 tabs (Building and Unit selection listers) for:
    @EmbeddedEntity
    UnitSelection unitSelection();

//  Note : While show ApplicationDTO - make tab with :
//  IList<PotentialTenantInfo> tenants();

    @Owned
    PetsDTO pets();
}
