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

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.crm.rpc.services.unit.UnitOccupancyCrudService;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper;

public class UnitOccupancyCrudServiceImpl extends AbstractCrudServiceImpl<AptUnitOccupancySegment> implements UnitOccupancyCrudService {

    public UnitOccupancyCrudServiceImpl() {
        super(AptUnitOccupancySegment.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(AptUnitOccupancySegment entity, AptUnitOccupancySegment dto) {
        if (dto.dateFrom().getValue().equals(AptUnitOccupancyManagerHelper.MIN_DATE)) {
            dto.dateFrom().setValue(null);
        }
        if (dto.dateTo().getValue().equals(AptUnitOccupancyManagerHelper.MAX_DATE)) {
            dto.dateTo().setValue(null);
        }
    }

    @Override
    protected void enhanceListRetrieved(AptUnitOccupancySegment entity, AptUnitOccupancySegment dto) {
        super.enhanceRetrieved(entity, dto);
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
