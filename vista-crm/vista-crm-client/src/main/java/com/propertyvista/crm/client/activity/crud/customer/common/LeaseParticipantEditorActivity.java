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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.prime.form.IEditor;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantEditorPresenter;
import com.propertyvista.crm.rpc.services.customer.LeaseParticipantCrudServiceBase;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseParticipantDTO;

public abstract class LeaseParticipantEditorActivity<DTO extends LeaseParticipantDTO<? extends LeaseTermParticipant<?>>, CS extends LeaseParticipantCrudServiceBase<DTO>>
        extends CrmEditorActivity<DTO> implements LeaseParticipantEditorPresenter<DTO> {

    public LeaseParticipantEditorActivity(CrudAppPlace place, IEditor<DTO> view, CS service, Class<DTO> dtoClass) {
        super(place, view, service, dtoClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getAllowedPaymentTypes(final AsyncCallback<Vector<PaymentType>> callback) {
        ((CS) getService()).getAllowedPaymentTypes(new DefaultAsyncCallback<Vector<PaymentType>>() {
            @Override
            public void onSuccess(Vector<PaymentType> result) {
                callback.onSuccess(result);
            }
        }, EntityFactory.createIdentityStub(getEntityClass(), getEntityId()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getCurrentAddress(final AsyncCallback<AddressSimple> callback) {
        ((CS) getService()).getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
            @Override
            public void onSuccess(AddressSimple result) {
                callback.onSuccess(result);
            }
        }, EntityFactory.createIdentityStub(getEntityClass(), getEntityId()));
    }
}
