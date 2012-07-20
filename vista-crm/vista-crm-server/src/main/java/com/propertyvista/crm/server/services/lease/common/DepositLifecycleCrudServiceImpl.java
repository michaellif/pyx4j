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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.lease.common.DepositLifecycleCrudService;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.DepositLifecycleDTO;

public class DepositLifecycleCrudServiceImpl extends AbstractCrudServiceDtoImpl<DepositLifecycle, DepositLifecycleDTO> implements DepositLifecycleCrudService {

    public DepositLifecycleCrudServiceImpl() {
        super(DepositLifecycle.class, DepositLifecycleDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(DepositLifecycle entity, DepositLifecycleDTO dto) {
        super.enhanceRetrieved(entity, dto);
        enhanceListRetrieved(entity, dto);

        // load detached:
        Persistence.service().retrieve(dto.interestAdjustments());
    }

    @Override
    protected void enhanceListRetrieved(DepositLifecycle entity, DepositLifecycleDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        // find corresponding deposit:
        EntityQueryCriteria<Deposit> criteria = new EntityQueryCriteria<Deposit>(Deposit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lifecycle(), entity));
        List<Deposit> deposits = Persistence.service().query(criteria);
        if (!deposits.isEmpty()) {
            Collections.sort(deposits, new Comparator<Deposit>() {
                @Override
                public int compare(Deposit o1, Deposit o2) {
                    return (int) (o1.getPrimaryKey().asLong() - o2.getPrimaryKey().asLong());
                }
            });
            dto.deposit().set(deposits.get(0));
        } else {
            throw new IllegalArgumentException();
        }

        // load detached:
        Persistence.service().retrieve(dto.deposit());
        Persistence.service().retrieve(dto.deposit().billableItem());
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
}
