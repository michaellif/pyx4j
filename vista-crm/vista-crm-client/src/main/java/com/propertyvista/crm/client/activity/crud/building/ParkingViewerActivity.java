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
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingViewerView;
import com.propertyvista.crm.rpc.services.building.ParkingCrudService;
import com.propertyvista.crm.rpc.services.building.ParkingSpotCrudService;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.dto.ParkingDTO;

public class ParkingViewerActivity extends CrmViewerActivity<ParkingDTO> implements ParkingViewerView.Presenter {

    private final ILister.Presenter<ParkingSpot> spotLister;

    public ParkingViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(ParkingViewerView.class), GWT.<ParkingCrudService> create(ParkingCrudService.class));

        spotLister = new ListerController<ParkingSpot>(((ParkingViewerView) getView()).getSpotView(),
                GWT.<ParkingSpotCrudService> create(ParkingSpotCrudService.class), ParkingSpot.class);

    }

    @Override
    public void onPopulateSuccess(ParkingDTO result) {
        super.onPopulateSuccess(result);

        spotLister.setParent(result.getPrimaryKey());
        spotLister.populate();
    }
}
