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

import com.propertyvista.crm.client.activity.crud.EditorActivityBase;
import com.propertyvista.crm.client.ui.crud.building.ElevatorEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.ElevatorCrudService;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorEditorActivity extends EditorActivityBase<ElevatorDTO> {

    @SuppressWarnings("unchecked")
    public ElevatorEditorActivity(Place place) {
        super((ElevatorEditorView) BuildingViewFactory.instance(ElevatorEditorView.class), (AbstractCrudService<ElevatorDTO>) GWT
                .create(ElevatorCrudService.class), ElevatorDTO.class);
        withPlace(place);
    }
}
