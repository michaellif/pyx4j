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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.RoofDTO;

public class RoofViewerActivity extends CrmViewerActivity<RoofDTO> {

    @SuppressWarnings("unchecked")
    public RoofViewerActivity(CrudAppPlace place) {
        super(place, BuildingViewFactory.instance(RoofViewerView.class), (AbstractCrudService<RoofDTO>) GWT.create(RoofCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.Mechanicals);
    }
}
