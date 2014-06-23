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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.SecureListerController;
import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity.ReturnBehaviour;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.billing.PaymentRecordCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public abstract class LeaseViewerActivityBase<DTO extends LeaseDTO> extends CrmViewerActivity<DTO> implements LeaseViewerViewBase.Presenter {

    private static final I18n i18n = I18n.get(LeaseViewerActivityBase.class);

    private final ILister.Presenter<PaymentRecordDTO> paymentLister;

    private final ReturnBehaviour returnBehaviour;

    protected DTO currentValue;

    public LeaseViewerActivityBase(CrudAppPlace place, IViewer<DTO> view, AbstractCrudService<DTO> service) {
        super(place, view, service);

        paymentLister = new SecureListerController<PaymentRecordDTO>(((LeaseViewerViewBase) getView()).getPaymentListerView(),
                GWT.<PaymentRecordCrudService> create(PaymentRecordCrudService.class), PaymentRecordDTO.class) {
            @Override
            public boolean canCreateNewItem() {
                return super.canCreateNewItem() && (currentValue.billingAccount().paymentAccepted().getValue() != BillingAccount.PaymentAccepted.DoNotAccept);
            }
        };

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

        populatePayments(result);
    }

    protected void populatePayments(Lease result) {
        paymentLister.setParent(result.billingAccount().getPrimaryKey());
        paymentLister.populate();
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
