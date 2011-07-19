/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.application;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;

public class ApplicationViewDelegate implements ApplicationView {

    private final IListerView<BuildingDTO> buildingLister;

    private final IListerView<AptUnitDTO> unitLister;

    private final IListerView<PotentialTenantInfo> tenantLister;

    public ApplicationViewDelegate(boolean readOnly) {
        buildingLister = new ListerInternalViewImplBase<BuildingDTO>(new SelectedBuildingLister(/* readOnly */));
        unitLister = new ListerInternalViewImplBase<AptUnitDTO>(new SelectedUnitLister(/* readOnly */));
        tenantLister = new ListerInternalViewImplBase<PotentialTenantInfo>(new PotentialTenantLister(/* readOnly */));
    }

    @Override
    public IListerView<BuildingDTO> getBuildingListerView() {
        return buildingLister;
    }

    @Override
    public IListerView<AptUnitDTO> getUnitListerView() {
        return unitLister;
    }

    @Override
    public IListerView<PotentialTenantInfo> getTenantListerView() {
        return tenantLister;
    }
}
