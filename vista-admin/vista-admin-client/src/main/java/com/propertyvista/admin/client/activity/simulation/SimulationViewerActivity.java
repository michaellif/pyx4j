/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.simulation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.admin.client.ui.crud.simulation.SimulationViewerView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.SimulationDTO;
import com.propertyvista.admin.rpc.services.SimulationService;

public class SimulationViewerActivity extends ViewerActivityBase<SimulationDTO> implements SimulationViewerView.Presenter {

    public SimulationViewerActivity(Place place) {
        super(place, AdministrationVeiwFactory.instance(SimulationViewerView.class), GWT.<AbstractCrudService<SimulationDTO>> create(SimulationService.class));
    }
}
