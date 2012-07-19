/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.common;

import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.form.IViewerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.deposit.DepositLifecycleListerActivity;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseViewerActivityBase<DTO extends LeaseDTO> extends CrmViewerActivity<DTO> implements LeaseViewerViewBase.Presenter {

    private final DepositLifecycleListerActivity depositLister;

    public LeaseViewerActivityBase(CrudAppPlace place, IViewerView<DTO> view, AbstractCrudService<DTO> service) {
        super(place, view, service);

        depositLister = new DepositLifecycleListerActivity(place, ((LeaseViewerViewBase<DTO>) getView()).getDepositListerView());
    }

    @Override
    public void retrieveUsers(final AsyncCallback<List<LeaseParticipant>> callback) {
        ((LeaseViewerCrudServiceBase<DTO>) getService()).retrieveUsers(new DefaultAsyncCallback<Vector<LeaseParticipant>>() {
            @Override
            public void onSuccess(Vector<LeaseParticipant> result) {
                callback.onSuccess(result);
            }
        }, getEntityId());
    }

    @Override
    public void onStop() {
        ((AbstractActivity) depositLister).onStop();
        super.onStop();
    }

    @Override
    protected void onPopulateSuccess(DTO result) {
        super.onPopulateSuccess(result);

        depositLister.setLeaseId(EntityFactory.createIdentityStub(Lease.class, result.getPrimaryKey()));
        depositLister.setParent(result.billingAccount().getPrimaryKey());
        depositLister.populate();
    }
}
