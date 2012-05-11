/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentViewerActivity extends CrmViewerActivity<PaymentRecordDTO> implements PaymentViewerView.Presenter {

    public PaymentViewerActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(PaymentViewerView.class), GWT.<AbstractCrudService<PaymentRecordDTO>> create(PaymentCrudService.class));
    }

    @Override
    public void processPayment() {
        ((PaymentCrudService) getService()).processPayment(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                populateView(result);
            }
        }, entityId);
    }

    @Override
    public void clearPayment() {
        ((PaymentCrudService) getService()).clearPayment(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                populateView(result);
            }
        }, entityId);
    }

    @Override
    public void rejectPayment() {
        ((PaymentCrudService) getService()).rejectPayment(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                populateView(result);
            }
        }, entityId);
    }

    @Override
    public void cancelPayment() {
        ((PaymentCrudService) getService()).cancelPayment(new DefaultAsyncCallback<PaymentRecordDTO>() {
            @Override
            public void onSuccess(PaymentRecordDTO result) {
                populateView(result);
            }
        }, entityId);
    }
}
