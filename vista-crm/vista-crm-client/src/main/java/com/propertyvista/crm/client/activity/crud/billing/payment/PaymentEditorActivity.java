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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentEditorView;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentEditorActivity extends CrmEditorActivity<PaymentRecordDTO> implements PaymentEditorView.Presenter {

    public PaymentEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(PaymentEditorView.class), GWT.<AbstractCrudService<PaymentRecordDTO>> create(PaymentCrudService.class),
                PaymentRecordDTO.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<PaymentRecordDTO> callback) {
        ((PaymentCrudService) getService()).initNewEntity(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                callback.onSuccess(result);
            }
        }, getParentId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getCurrentAddress(final AsyncCallback<AddressStructured> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> payer) {
        ((PaymentCrudService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
            @Override
            public void onSuccess(AddressStructured result) {
                callback.onSuccess(result);
            }
        }, (LeaseTermParticipant<? extends LeaseParticipant<?>>) payer.createIdentityStub());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<LeasePaymentMethod>> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> payer) {
        ((PaymentCrudService) getService()).getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(Vector<LeasePaymentMethod> result) {
                callback.onSuccess(result);
            }
        }, (LeaseTermParticipant<? extends LeaseParticipant<?>>) payer.createIdentityStub());
    }
}
