/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.billing;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.PaymentRecordDTO;

public interface PaymentCrudService extends AbstractCrudService<PaymentRecordDTO> {

    void initNewEntity(AsyncCallback<PaymentRecordDTO> callback, Key parentId);

    void getCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> payer);

    void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback, LeaseTermParticipant<? extends LeaseParticipant<?>> payer);

    // Payment operations:

    void schedulePayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId);

    void processPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId);

    void clearPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId);

    void rejectPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId, boolean applyNSF);

    void cancelPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId);
}
