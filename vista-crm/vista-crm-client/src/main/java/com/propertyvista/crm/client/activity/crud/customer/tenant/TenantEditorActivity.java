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
package com.propertyvista.crm.client.activity.crud.customer.tenant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.dto.TenantDTO;

public class TenantEditorActivity extends EditorActivityBase<TenantDTO> implements TenantEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public TenantEditorActivity(CrudAppPlace place) {
        super(place, CustomerViewFactory.instance(TenantEditorView.class), (AbstractCrudService<TenantDTO>) GWT.create(TenantCrudService.class),
                TenantDTO.class);
    }

    @Override
    public void deletePaymentMethod(PaymentMethod paymentMethod) {
        ((TenantCrudService) getService()).deletePaymentMethod(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
        }, paymentMethod);
    }

    @Override
    public void getCurrentAddress(final AsyncCallback<AddressStructured> callback) {
        ((TenantCrudService) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
            @Override
            public void onSuccess(AddressStructured result) {
                callback.onSuccess(result);
            }
        }, getEntityId());
    }
}
