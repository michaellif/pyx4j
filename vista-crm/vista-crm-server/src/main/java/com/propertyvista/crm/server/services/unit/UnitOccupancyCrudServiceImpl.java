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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.services.unit.UnitOccupancyCrudService;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

public class UnitOccupancyCrudServiceImpl extends AbstractCrudServiceImpl<AptUnitOccupancySegment> implements UnitOccupancyCrudService {

    public UnitOccupancyCrudServiceImpl() {
        super(AptUnitOccupancySegment.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<AptUnitOccupancySegment>> callback, EntityListCriteria<AptUnitOccupancySegment> dtoCriteria) {
        dtoCriteria.asc(toProto.dateFrom());
        super.list(callback, dtoCriteria);
    }

    @Override
    protected void enhanceRetrieved(AptUnitOccupancySegment bo, AptUnitOccupancySegment to, RetrieveTarget retrieveTarget) {
        if (to.dateFrom().getValue().equals(OccupancyFacade.MIN_DATE)) {
            to.dateFrom().setValue(null);
        }
        if (to.dateTo().getValue().equals(OccupancyFacade.MAX_DATE)) {
            to.dateTo().setValue(null);
        }
    }

    @Override
    protected void enhanceListRetrieved(AptUnitOccupancySegment entity, AptUnitOccupancySegment dto) {
        this.enhanceRetrieved(entity, dto, null);
    }

    @Override
    protected boolean persist(AptUnitOccupancySegment bo, AptUnitOccupancySegment to) {
        throw new UnsupportedOperationException();
    }

}
