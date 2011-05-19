/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.listers;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import com.propertyvista.crm.client.ui.listers.IBuildingListerView;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingListerActivity extends ListerActivityBase<Building> {

    @Inject
    @SuppressWarnings("unchecked")
    public BuildingListerActivity(IBuildingListerView view) {
        super(view, (AbstractCrudService<Building>) GWT.create(BuildingCrudService.class), Building.class);
    }

}
