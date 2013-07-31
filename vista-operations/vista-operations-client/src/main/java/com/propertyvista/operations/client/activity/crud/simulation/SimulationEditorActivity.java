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
package com.propertyvista.operations.client.activity.crud.simulation;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulation.SimulationEdtiorView;
import com.propertyvista.operations.rpc.SimulationDTO;
import com.propertyvista.operations.rpc.services.simulator.SimulationService;

public class SimulationEditorActivity extends AbstractEditorActivity<SimulationDTO> implements SimulationEdtiorView.Presenter {

    public SimulationEditorActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().instantiate(SimulationEdtiorView.class), GWT
                .<AbstractCrudService<SimulationDTO>> create(SimulationService.class), SimulationDTO.class);
    }

}
