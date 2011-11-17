/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.ParkingSpotCrudService;
import com.propertyvista.domain.property.asset.ParkingSpot;

public class ParkingSpotListerActivity extends ListerActivityBase<ParkingSpot> {

    @SuppressWarnings("unchecked")
    public ParkingSpotListerActivity(Place place) {
        super(place, BuildingViewFactory.instance(ParkingSpotListerView.class), (AbstractCrudService<ParkingSpot>) GWT.create(ParkingSpotCrudService.class),
                ParkingSpot.class);
    }
}
