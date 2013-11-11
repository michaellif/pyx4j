/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaViewerView;
import com.propertyvista.crm.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.building.LockerCrudService;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaViewerActivity extends CrmViewerActivity<LockerAreaDTO> implements LockerAreaViewerView.Presenter {

    private final ILister.Presenter<?> lockerLister;

    public LockerAreaViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(LockerAreaViewerView.class), GWT.<LockerAreaCrudService> create(LockerAreaCrudService.class));

        lockerLister = new ListerController<Locker>(((LockerAreaViewerView) getView()).getLockerView(),
                GWT.<LockerCrudService> create(LockerCrudService.class), Locker.class);

    }

    @Override
    public void onPopulateSuccess(LockerAreaDTO result) {
        super.onPopulateSuccess(result);

        lockerLister.setParent(result.getPrimaryKey());
        lockerLister.populate();
    }
}
