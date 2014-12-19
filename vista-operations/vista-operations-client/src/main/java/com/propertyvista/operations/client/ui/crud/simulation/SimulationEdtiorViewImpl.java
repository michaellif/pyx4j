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
 */
package com.propertyvista.operations.client.ui.crud.simulation;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.rpc.dto.SimulationDTO;

public class SimulationEdtiorViewImpl extends OperationsEditorViewImplBase<SimulationDTO> implements SimulationEdtiorView {

    public SimulationEdtiorViewImpl() {
        setForm(new SimulationForm(this));
    }
}
