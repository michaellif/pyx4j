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
package com.propertyvista.crm.client.activity.crud.lease.common.deposit;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.ui.crud.lease.common.deposit.DepositListerPresenter;
import com.propertyvista.crm.rpc.services.lease.common.DepositCrudService;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositStatus;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;

public class DepositListerActivity extends ListerActivityBase<Deposit> implements DepositListerPresenter {

    public DepositListerActivity(Place place, IListerView<Deposit> view) {
        super(place, view, GWT.<DepositCrudService> create(DepositCrudService.class), Deposit.class);
    }

    @Override
    public void getLeaseBillableItems(final AsyncCallback<List<BillableItem>> callback) {
        ((DepositCrudService) getService()).getLeaseBillableItems(new DefaultAsyncCallback<Vector<BillableItem>>() {
            @Override
            public void onSuccess(Vector<BillableItem> result) {
                callback.onSuccess(result);
            }
        }, EntityFactory.createIdentityStub(BillingAccount.class, getParent()));
    }

    @Override
    public void createDeposit(final AsyncCallback<Deposit> callback, final DepositType depositType, BillableItem itemId) {
        ((DepositCrudService) getService()).createDeposit(new DefaultAsyncCallback<Deposit>() {
            @Override
            public void onSuccess(Deposit result) {
                if (result == null) { // if there is no deposits of such type - create it 'on the fly':
                    result = EntityFactory.create(Deposit.class);
                    result.type().setValue(depositType);
                    result.status().setValue(DepositStatus.Created);
                    result.depositDate().setValue(new LogicalDate());
                    result.billingAccount().set(EntityFactory.createIdentityStub(BillingAccount.class, getParent()));
                }
                callback.onSuccess(result);
            }
        }, depositType, itemId, EntityFactory.createIdentityStub(BillingAccount.class, getParent()));
    }
}
