/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.application;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.application.ApplicationView;
import com.propertyvista.crm.rpc.services.ApplicationTenantCrudService;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;

public class ApplicationActivityDelegate implements ApplicationView.Presenter {

    private final IListerView.Presenter buildingsLister;

    private final IListerView.Presenter unitsLister;

    private final IListerView.Presenter tenantsLister;

    @SuppressWarnings("unchecked")
    public ApplicationActivityDelegate(ApplicationView view) {

        buildingsLister = new ListerActivityBase<BuildingDTO>(view.getBuildingListerView(),
                (AbstractCrudService<BuildingDTO>) GWT.create(BuildingCrudService.class), BuildingDTO.class);

        unitsLister = new ListerActivityBase<AptUnitDTO>(view.getUnitListerView(), (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class),
                AptUnitDTO.class);

        tenantsLister = new ListerActivityBase<PotentialTenantInfo>(view.getTenantListerView(),
                (AbstractCrudService<PotentialTenantInfo>) GWT.create(ApplicationTenantCrudService.class), PotentialTenantInfo.class);
    }

    public void populate(ApplicationDTO current) {

        buildingsLister.populateData(0);

        if (!current.selectedBuilding().isEmpty()) {
            unitsLister.setParentFiltering(current.selectedBuilding().getPrimaryKey());
        }
        unitsLister.populateData(0);

        tenantsLister.populateData(0);
    }

    @Override
    public Presenter getBuildingPresenter() {
        return buildingsLister;
    }

    @Override
    public Presenter getUnitPresenter() {
        return unitsLister;
    }

    @Override
    public Presenter getTenantPresenter() {
        return tenantsLister;
    }
}
