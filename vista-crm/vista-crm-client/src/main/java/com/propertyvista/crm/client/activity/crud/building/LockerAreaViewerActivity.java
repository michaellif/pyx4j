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
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.building.LockerAreaViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.LockerAreaCrudService;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaViewerActivity extends ViewerActivityBase<LockerAreaDTO> {

    @SuppressWarnings("unchecked")
    public LockerAreaViewerActivity(Place place) {
        super((LockerAreaViewerView) BuildingViewFactory.instance(LockerAreaViewerView.class), (AbstractCrudService<LockerAreaDTO>) GWT
                .create(LockerAreaCrudService.class));
        withPlace(place);
    }

}
