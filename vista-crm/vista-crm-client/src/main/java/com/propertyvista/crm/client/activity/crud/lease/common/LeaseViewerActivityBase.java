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
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity.ReturnBehaviour;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseViewerActivityBase<DTO extends LeaseDTO> extends CrmViewerActivity<DTO> implements LeaseViewerViewBase.Presenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivityBase.class);

    private final ReturnBehaviour returnBehaviour;

    public LeaseViewerActivityBase(CrudAppPlace place, IViewer<DTO> view, AbstractCrudService<DTO> service) {
        super(place, view, service);

        if (service instanceof LeaseViewerCrudService) {
            returnBehaviour = ReturnBehaviour.Lease;
        } else if (service instanceof LeaseApplicationViewerCrudService) {
            returnBehaviour = ReturnBehaviour.Application;
        } else {
            returnBehaviour = ReturnBehaviour.Default;
        }
    }

    @Override
    public void retrieveUsers(final AsyncCallback<List<LeaseTermParticipant<?>>> callback) {
        ((LeaseViewerCrudServiceBase<DTO>) getService()).retrieveUsers(new DefaultAsyncCallback<Vector<LeaseTermParticipant<?>>>() {
            @Override
            public void onSuccess(Vector<LeaseTermParticipant<?>> result) {
                callback.onSuccess(result);
            }
        }, getEntityId());
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
    public void setLegalStatus() {
    }

    @Override
    public void clearLegalStatus() {
        // TODO Auto-generated method stub

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
    public void unreserveUnit() {
        ((LeaseViewerCrudServiceBase<DTO>) getService()).unreserveUnit(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }
}
