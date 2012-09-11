/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMeta;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseExpirationGadgetService extends IService {

    void leaseExpriation(AsyncCallback<LeaseExpirationGadgetDataDTO> callback, Vector<Building> buildings);

    void makeLeaseFilterCriteria(AsyncCallback<EntityListCriteria<LeaseDTO>> callback, Vector<Building> buildingsFilter,
            LeaseExpirationGadgetMeta.LeaseFilter leaseFilter);

    void makeOccupiedUnitsFilterCriteria(AsyncCallback<EntityListCriteria<AptUnitDTO>> callback, Vector<Building> buildingsFilter);

}
