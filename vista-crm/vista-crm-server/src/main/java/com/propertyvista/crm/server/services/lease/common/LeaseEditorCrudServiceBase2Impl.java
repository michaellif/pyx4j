/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.LeaseFacade2;
import com.propertyvista.crm.rpc.services.lease.common.LeaseEditorCrudServiceBase2;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.dto.LeaseDTO2;

public abstract class LeaseEditorCrudServiceBase2Impl<DTO extends LeaseDTO2> extends LeaseCrudServiceBase2Impl<DTO> implements LeaseEditorCrudServiceBase2<DTO> {

    protected LeaseEditorCrudServiceBase2Impl(Class<DTO> dtoClass) {
        super(dtoClass);
    }

    @Override
    protected void enhanceRetrieved(Lease2 in, DTO dto) {
        super.enhanceRetrieved(in, dto);
    }

    @Override
    protected void create(Lease2 dbo, DTO dto) {
        ServerSideFactory.create(LeaseFacade2.class).init(dbo);
        save(dbo, dto);
//        ServerSideFactory.create(LeaseFacade2.class).persist(dbo.currentLeaseTerm());
    }

    @Override
    protected void save(Lease2 dbo, DTO in) {
        updateCurrentTermDates(dbo);
        ServerSideFactory.create(LeaseFacade2.class).persist(dbo);
    }

    @Override
    public void setSelectedUnit(AsyncCallback<DTO> callback, AptUnit unitId, DTO currentValue) {
        ServerSideFactory.create(LeaseFacade2.class).setUnit(currentValue, unitId);
        callback.onSuccess(currentValue);
    }

    private void updateCurrentTermDates(Lease2 lease) {
        lease.currentLeaseTerm().termFrom().set(lease.leaseFrom());
        lease.currentLeaseTerm().termTo().set(lease.leaseTo());
    }
}