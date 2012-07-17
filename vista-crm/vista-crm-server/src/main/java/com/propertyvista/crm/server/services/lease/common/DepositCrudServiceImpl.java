/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease.common;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.crm.rpc.services.lease.common.DepositCrudService;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Lease;

public class DepositCrudServiceImpl extends AbstractCrudServiceImpl<Deposit> implements DepositCrudService {

    public DepositCrudServiceImpl() {
        super(Deposit.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Deposit entity, Deposit dto) {
        super.enhanceRetrieved(entity, dto);

        // load detached:
        Persistence.service().retrieve(dto.billableItem());
        Persistence.service().retrieve(dto.interestAdjustments());
    }

    @Override
    protected void enhanceListRetrieved(Deposit entity, Deposit dto) {
        super.enhanceListRetrieved(entity, dto);

        // load detached:
        Persistence.service().retrieve(dto.billableItem());
    }

    @Override
    public void getLeaseBillableItems(AsyncCallback<Vector<BillableItem>> callback, Lease leaseId) {
        Vector<BillableItem> items = new Vector<BillableItem>();

        Persistence.service().retrieve(leaseId);
        items.add((BillableItem) leaseId.version().leaseProducts().serviceItem().detach());
        for (BillableItem item : leaseId.version().leaseProducts().featureItems()) {
            items.add((BillableItem) item.detach());
        }

        callback.onSuccess(items);
    }

    @Override
    public void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem itemId, Lease leaseId) {
        Persistence.service().retrieve(leaseId);
        callback.onSuccess(ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, itemId, leaseId.unit().building()));
    }
}
