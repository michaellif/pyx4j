/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.unit;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.crm.rpc.services.unit.UnitOccupancyManagerService;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;

public class UnitOccupancyManagerServiceImpl implements UnitOccupancyManagerService {

    @Override
    public void scopeOffMarket(AsyncCallback<VoidSerializable> callback, Key unitPk, OffMarketType type) {
        ServerSideFactory.create(OccupancyFacade.class).scopeOffMarket(unitPk, type);
        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void scopeRenovation(AsyncCallback<VoidSerializable> callback, Key unitPk, LogicalDate renovationEndDate) {
        ServerSideFactory.create(OccupancyFacade.class).scopeRenovation(unitPk, renovationEndDate);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void scopeAvailable(AsyncCallback<VoidSerializable> callback, Key unitPk) {
        ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(unitPk);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void makeVacant(AsyncCallback<VoidSerializable> callback, Key unitPk, LogicalDate vacantFrom) {
        ServerSideFactory.create(OccupancyFacade.class).makeVacant(unitPk, vacantFrom);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void canScopeOffMarket(AsyncCallback<Boolean> callback, Key unitPk) {
        callback.onSuccess(ServerSideFactory.create(OccupancyFacade.class).isScopeOffMarketAvailable(unitPk));
    }

    @Override
    public void canScopeRenovation(AsyncCallback<LogicalDate> callback, Key unitPk) {
        callback.onSuccess(ServerSideFactory.create(OccupancyFacade.class).isRenovationAvailable(unitPk));
    }

    @Override
    public void canScopeAvailable(AsyncCallback<Boolean> callback, Key unitPk) {
        callback.onSuccess(ServerSideFactory.create(OccupancyFacade.class).isScopeAvailableAvailable(unitPk));
    }

    @Override
    public void getMakeVacantConstraints(AsyncCallback<MakeVacantConstraintsDTO> callback, Key unitPk) {
        callback.onSuccess(ServerSideFactory.create(OccupancyFacade.class).getMakeVacantConstraints(unitPk));
    }

}
