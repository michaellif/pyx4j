/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.field.client.activity.crud.building;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.field.client.ui.crud.building.BuildingListerView;
import com.propertyvista.field.client.ui.viewfactories.BuildingViewFactory;
import com.propertyvista.field.rpc.services.building.BuildingCrudService;

public class BuildingListerActivity extends AbstractListerActivity<BuildingDTO> {

    @SuppressWarnings("unchecked")
    public BuildingListerActivity(Place place) {
        super(place, BuildingViewFactory.instance(BuildingListerView.class), (AbstractCrudService<BuildingDTO>) GWT.create(BuildingCrudService.class),
                BuildingDTO.class);
    }

    @Override
    public boolean canCreateNewItem() {
        return SecurityController.checkBehavior(VistaCrmBehavior.PropertyManagement);
    }

    @Override
    public void onStop() {
        getView().storeState(getView().getMemento().getCurrentPlace());
    }

}
