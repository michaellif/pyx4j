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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.crud.tenant.SelectTenantLister;
import com.propertyvista.crm.client.ui.crud.unit.SelectedUnitLister;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;

public class LeaseViewDelegate implements LeaseView {

    private final IListerView<Building> buildingLister;

    private final IListerView<AptUnit> unitLister;

    private final IListerView<Tenant> tenantLister;

    public LeaseViewDelegate(boolean readOnly) {
        buildingLister = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister(/* readOnly */));
        unitLister = new ListerInternalViewImplBase<AptUnit>(new SelectedUnitLister(/* readOnly */));
        tenantLister = new ListerInternalViewImplBase<Tenant>(new SelectTenantLister(/* readOnly */));
    }

    @Override
    public IListerView<Building> getBuildingListerView() {
        return buildingLister;
    }

    @Override
    public IListerView<AptUnit> getUnitListerView() {
        return unitLister;
    }

    @Override
    public IListerView<Tenant> getTenantListerView() {
        return tenantLister;
    }
}
