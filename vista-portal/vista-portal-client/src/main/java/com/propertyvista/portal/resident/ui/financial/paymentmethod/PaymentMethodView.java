/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 */
package com.propertyvista.portal.resident.ui.financial.paymentmethod;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.shared.ui.IEditorView;

public interface PaymentMethodView extends IEditorView<PaymentMethodDTO> {

    public interface Presenter extends IEditorPresenter<PaymentMethodDTO> {

        void getCurrentAddress(AsyncCallback<InternationalAddress> callback);
    }
}
