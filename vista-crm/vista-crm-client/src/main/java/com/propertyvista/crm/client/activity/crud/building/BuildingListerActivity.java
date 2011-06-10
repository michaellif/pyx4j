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
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import com.propertyvista.crm.client.activity.crud.ListerActivityBase;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerView;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerActivity extends ListerActivityBase<BuildingDTO> {

    @Inject
    @SuppressWarnings("unchecked")
    public BuildingListerActivity(BuildingListerView view) {
        super(view, (AbstractCrudService<BuildingDTO>) GWT.create(BuildingCrudService.class), BuildingDTO.class);
    }
}
