/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-15
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.common;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.prime.form.IEditor;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public interface LeaseParticipantEditorPresenter<E extends LeaseParticipant<?>> extends IEditor.Presenter {

    void getAllowedPaymentTypes(AsyncCallback<Vector<PaymentType>> callback);

    void getCurrentAddress(AsyncCallback<AddressSimple> callback);
}