/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing.payment;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentRecordEditorView;
import com.propertyvista.crm.rpc.services.billing.PaymentRecordCrudService;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentRecordEditorActivity extends CrmEditorActivity<PaymentRecordDTO> implements PaymentRecordEditorView.Presenter {

    public PaymentRecordEditorActivity(CrudAppPlace place) {
        super(PaymentRecordDTO.class, place, CrmSite.getViewFactory().getView(PaymentRecordEditorView.class), GWT
                        .<AbstractCrudService<PaymentRecordDTO>> create(PaymentRecordCrudService.class));
    }

    @Override
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        PaymentRecordCrudService.PaymentInitializationData id = EntityFactory.create(PaymentRecordCrudService.PaymentInitializationData.class);
        id.parent().set(EntityFactory.createIdentityStub(BillingAccount.class, getParentId()));
        callback.onSuccess(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getCurrentAddress(final AsyncCallback<InternationalAddress> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> payer) {
        ((PaymentRecordCrudService) getService()).getCurrentAddress(new DefaultAsyncCallback<InternationalAddress>() {
            @Override
            public void onSuccess(InternationalAddress result) {
                callback.onSuccess(result);
            }
        }, (LeaseTermParticipant<? extends LeaseParticipant<?>>) payer.createIdentityStub());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<LeasePaymentMethod>> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> payer) {
        ((PaymentRecordCrudService) getService()).getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(Vector<LeasePaymentMethod> result) {
                callback.onSuccess(result);
            }
        }, (LeaseTermParticipant<? extends LeaseParticipant<?>>) payer.createIdentityStub());
    }
}
