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
package com.propertyvista.crm.client.activity.crud.billing;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentEditorActivity extends EditorActivityBase<PaymentRecordDTO> implements PaymentEditorView.Presenter {

    public PaymentEditorActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(PaymentEditorView.class), GWT.<AbstractCrudService<PaymentRecordDTO>> create(PaymentCrudService.class),
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

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressStructured> callback, LeaseParticipant payer) {
        ((PaymentCrudService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
            @Override
            public void onSuccess(AddressStructured result) {
                callback.onSuccess(result);
            }
        }, (LeaseParticipant) payer.createIdentityStub());
    }

    @Override
    public void getDefaultPaymentMethod(final AsyncCallback<PaymentMethod> callback, LeaseParticipant payer) {
        ((PaymentCrudService) getService()).getDefaultPaymentMethod(new DefaultAsyncCallback<PaymentMethod>() {
            @Override
            public void onSuccess(PaymentMethod result) {
                callback.onSuccess(result);
            }
        }, (LeaseParticipant) payer.createIdentityStub());
    }

    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<PaymentMethod>> callback, LeaseParticipant payer) {
        ((PaymentCrudService) getService()).getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<PaymentMethod>>() {
            @Override
            public void onSuccess(Vector<PaymentMethod> result) {
                callback.onSuccess(result);
            }
        }, (LeaseParticipant) payer.createIdentityStub());
    }
}
