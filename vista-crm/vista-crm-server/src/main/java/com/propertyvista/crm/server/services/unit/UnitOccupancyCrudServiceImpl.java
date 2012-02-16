/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.unit;

import javax.naming.OperationNotSupportedException;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.crm.rpc.services.unit.UnitOccupancyCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceImpl;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper;

public class UnitOccupancyCrudServiceImpl extends GenericCrudServiceImpl<AptUnitOccupancySegment> implements UnitOccupancyCrudService {

    public UnitOccupancyCrudServiceImpl() {
        super(AptUnitOccupancySegment.class);
    }

    @Override
    protected void enhanceRetrieved(AptUnitOccupancySegment entity, boolean fromList) {
        super.enhanceRetrieved(entity, fromList);
        if (entity.dateFrom().getValue().equals(AptUnitOccupancyManagerHelper.MIN_DATE)) {
            entity.dateFrom().setValue(null);
        }
        if (entity.dateTo().getValue().equals(AptUnitOccupancyManagerHelper.MAX_DATE)) {
            entity.dateTo().setValue(null);
        }
    }

    @Override
    public void save(AsyncCallback<AptUnitOccupancySegment> callback, AptUnitOccupancySegment entity) {
        callback.onFailure(new OperationNotSupportedException());
    }

    @Override
    public void create(AsyncCallback<AptUnitOccupancySegment> callback, AptUnitOccupancySegment entity) {
        callback.onFailure(new OperationNotSupportedException());
    }
}
