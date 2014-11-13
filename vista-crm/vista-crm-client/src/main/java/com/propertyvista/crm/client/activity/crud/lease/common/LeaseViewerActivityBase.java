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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.form.IViewerView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity.ReturnBehaviour;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseViewerActivityBase<DTO extends LeaseDTO> extends CrmViewerActivity<DTO> implements LeaseViewerViewBase.Presenter {

    private final ReturnBehaviour returnBehaviour;

    protected DTO currentValue;

    public LeaseViewerActivityBase(Class<DTO> entityClass, CrudAppPlace place, IViewerView<DTO> view, AbstractCrudService<DTO> service) {
        super(entityClass, place, view, service);

        if (service instanceof LeaseViewerCrudService) {
            returnBehaviour = ReturnBehaviour.Lease;
        } else if (service instanceof LeaseApplicationViewerCrudService) {
            returnBehaviour = ReturnBehaviour.Application;
        } else {
            returnBehaviour = ReturnBehaviour.Default;
        }
    }

    @Override
    protected void onPopulateSuccess(DTO result) {
        super.onPopulateSuccess(result);
        currentValue = result;
    }

    @Override
    public void retrieveParticipants(final AsyncCallback<List<LeaseTermParticipant<?>>> callback, Boolean includeDependants) {
        ((LeaseViewerCrudServiceBase<DTO>) getService()).retrieveParticipants(new DefaultAsyncCallback<Vector<LeaseTermParticipant<?>>>() {
            @Override
            public void onSuccess(Vector<LeaseTermParticipant<?>> result) {
                callback.onSuccess(result);
            }
        }, getEntityId(), includeDependants);
    }

    @Override
    public void navigateParticipant(List<LeaseTermParticipant<?>> users) {
        assert (!users.isEmpty());
        if (users.get(0) instanceof LeaseTermTenant) {
            AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Tenant().formViewerPlace(users.get(0).leaseParticipant().getPrimaryKey()));
        } else if (users.get(0) instanceof LeaseTermGuarantor) {
            AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Guarantor().formViewerPlace(users.get(0).leaseParticipant().getPrimaryKey()));
        } else {
            throw new IllegalArgumentException("Unsupported Participant type");
        }
    }

    @Override
    public void viewTerm(LeaseTerm leaseTermId) {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseTerm().formViewerPlace(leaseTermId.getPrimaryKey()));
    }

    @Override
    public void editTerm(LeaseTerm leaseTermId) {
        AppSite.getPlaceController().goTo(
                new CrmSiteMap.Tenants.LeaseTerm().formEditorPlace(leaseTermId.getPrimaryKey()).queryArg(LeaseTermEditorActivity.ARG_NAME_RETURN_BH,
                        returnBehaviour.name()));
    }

    @Override
    public void reserveUnit(int durationHours) {
        ((LeaseViewerCrudServiceBase<DTO>) getService()).reserveUnit(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId(), durationHours);
    }

    @Override
    public void releaseUnit() {
        ((LeaseViewerCrudServiceBase<DTO>) getService()).releaseUnit(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void newPayment() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Finance.Payment().formNewItemPlace(currentValue.billingAccount().getPrimaryKey()));
    }
}
