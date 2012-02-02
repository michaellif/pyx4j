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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.crm.client.ui.crud.building.mech.BoilerViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.building.mech.BoilerCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.BoilerDTO;

public class BoilerViewerActivity extends ViewerActivityBase<BoilerDTO> {

    @SuppressWarnings("unchecked")
    public BoilerViewerActivity(Place place) {
        super(place, BuildingViewFactory.instance(BoilerViewerView.class), (AbstractCrudService<BoilerDTO>) GWT.create(BoilerCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.Mechanicals);
    }
}
