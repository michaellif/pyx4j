/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.payment;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.crud.form.IEditorView;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.PaymentRecordDTO;

public interface PaymentEditorView extends IEditorView<PaymentRecordDTO> {

    interface Presenter extends IEditorView.Presenter {

        void getCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseTermParticipant payer);

        void getProfiledPaymentMethods(AsyncCallback<List<PaymentMethod>> callback, LeaseTermParticipant payer);
    }
}
