/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.dto.gadgets.LeadsAndRentalsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeadsAndRentalsGadgetService;
import com.propertyvista.domain.property.asset.building.Building;

public class LeadsAndRentalsGadgetServiceImpl implements LeadsAndRentalsGadgetService {

    @Override
    public void countData(AsyncCallback<LeadsAndRentalsGadgetDataDTO> callback, Vector<Building> queryParams) {
        LeadsAndRentalsGadgetDataDTO data = EntityFactory.create(LeadsAndRentalsGadgetDataDTO.class);
        data.leads().setValue(53);
        data.appointments().setValue(25);
        data.rentals().setValue(5);
        callback.onSuccess(data);
    }

}
