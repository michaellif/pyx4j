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
package com.propertyvista.crm.client.activity.crud.customer.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantEditorPresenter;
import com.propertyvista.crm.rpc.services.customer.LeaseParticipantCrudServiceBase;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseParticipantDTO;

public abstract class LeaseParticipantEditorActivity<E extends LeaseTermParticipant<?>, DTO extends LeaseParticipantDTO<E>, CS extends LeaseParticipantCrudServiceBase<E, DTO>>
        extends CrmEditorActivity<DTO> implements LeaseParticipantEditorPresenter {

    public LeaseParticipantEditorActivity(CrudAppPlace place, IEditorView<DTO> view, CS service, Class<DTO> dtoClass) {
        super(place, view, service, dtoClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deletePaymentMethod(LeasePaymentMethod paymentMethod) {
        ((CS) getService()).deletePaymentMethod(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
        }, paymentMethod);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getCurrentAddress(final AsyncCallback<AddressStructured> callback) {
        ((CS) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
            @Override
            public void onSuccess(AddressStructured result) {
                callback.onSuccess(result);
            }
        }, getEntityId());
    }
}
