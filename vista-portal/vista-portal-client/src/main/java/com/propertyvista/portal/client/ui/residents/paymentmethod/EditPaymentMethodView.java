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
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.paymentmethod;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.client.ui.residents.View;

public interface EditPaymentMethodView extends View<LeasePaymentMethod> {

    interface Presenter extends View.Presenter<LeasePaymentMethod> {

        void getCurrentAddress(AsyncCallback<AddressStructured> callback);
    }
}
