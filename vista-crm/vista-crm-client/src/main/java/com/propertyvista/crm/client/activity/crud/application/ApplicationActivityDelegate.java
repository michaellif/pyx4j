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

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.application.ApplicationView;
import com.propertyvista.crm.rpc.services.ApplicationTenantCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;

public class ApplicationActivityDelegate implements ApplicationView.Presenter {

    private final IListerView.Presenter unitsLister;

    private final IListerView.Presenter tenantsLister;

    @SuppressWarnings("unchecked")
    public ApplicationActivityDelegate(ApplicationView view) {

        unitsLister = new ListerActivityBase<AptUnitDTO>(view.getUnitListerView(), (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class),
                AptUnitDTO.class);

        tenantsLister = new ListerActivityBase<PotentialTenantInfo>(view.getTenantListerView(),
                (AbstractCrudService<PotentialTenantInfo>) GWT.create(ApplicationTenantCrudService.class), PotentialTenantInfo.class);
    }

    public void populate(Key parentID) {

//        unitsLister.setParentFiltering(parentID);
        unitsLister.populateData(0);

//        tenantsLister.setParentFiltering(parentID);
        tenantsLister.populateData(0);
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
