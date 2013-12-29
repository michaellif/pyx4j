/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.gadgets.MaintenanceGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.MaintenanceRequestCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.MaintenanceRequestDTO;

public interface MaintenanceGadgetService extends AbstractCounterGadgetBaseService<MaintenanceGadgetDataDTO, Vector<Building>>,
        MaintenanceRequestCriteriaProvider {

    @Override
    public void countData(AsyncCallback<MaintenanceGadgetDataDTO> callback, Vector<Building> buildingsFilter);

    @Override
    public void makeMaintenaceRequestCriteria(AsyncCallback<EntityListCriteria<MaintenanceRequestDTO>> callback, Vector<Building> buildingsFilter, String preset);
}
