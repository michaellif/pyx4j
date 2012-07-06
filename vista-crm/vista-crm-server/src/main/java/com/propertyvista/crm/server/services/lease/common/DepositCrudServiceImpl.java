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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.crm.rpc.services.lease.common.DepositCrudService;
import com.propertyvista.domain.financial.BillingAccount;
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
        Persistence.service().retrieve(dto.interestAdjustments());
    }

    @Override
    public void getLeaseBillableItems(AsyncCallback<Vector<BillableItem>> callback, BillingAccount billingAccountId) {
        Vector<BillableItem> items = new Vector<BillableItem>();

        EntityQueryCriteria<Lease> criteria = new EntityQueryCriteria<Lease>(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccountId));
        Lease lease = Persistence.service().retrieve(criteria);
        assert lease != null;
        items.add((BillableItem) lease.version().leaseProducts().serviceItem().detach());
        for (BillableItem item : lease.version().leaseProducts().featureItems()) {
            items.add((BillableItem) item.detach());
        }

        callback.onSuccess(items);
    }

    @Override
    public void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem itemId, BillingAccount billingAccountId) {
        EntityQueryCriteria<Lease> criteria = new EntityQueryCriteria<Lease>(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccountId));
        Lease lease = Persistence.service().retrieve(criteria);
        assert lease != null;
        callback.onSuccess(ServerSideFactory.create(DepositFacade.class).createDeposit(depositType, itemId, lease.unit().building()));
    }
}
