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
package com.propertyvista.crm.rpc.services.unit;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;

public interface UnitOccupancyManagerService extends IService {

    void scopeOffMarket(AsyncCallback<VoidSerializable> callback, Key unitPk, OffMarketType type);

    void canScopeOffMarket(AsyncCallback<Boolean> callback, Key unitPk);

    void scopeRenovation(AsyncCallback<VoidSerializable> callback, Key unitPk, LogicalDate renovationEndDate);

    /**
     * callback holds value or minimal renovation end date
     */
    void canScopeRenovation(AsyncCallback<LogicalDate> callback, Key unitPk);

    void scopeAvailable(AsyncCallback<VoidSerializable> callback, Key unitPk);

    void canScopeAvailable(AsyncCallback<Boolean> callback, Key unitPk);

    void makeVacant(AsyncCallback<VoidSerializable> callback, Key unitPk, LogicalDate vacantFrom);

    void getMakeVacantConstraints(AsyncCallback<MakeVacantConstraintsDTO> callback, Key unitPk);

}
