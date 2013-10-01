/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.services.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodSummaryDTO;

public interface PaymentService extends IService {

    void retrievePaymentMethod(AsyncCallback<PaymentMethodDTO> callback, LeasePaymentMethod itemId);

    void deletePaymentMethod(AsyncCallback<Boolean> callback, LeasePaymentMethod itemId);

    void getPaymentMethodSummary(AsyncCallback<PaymentMethodSummaryDTO> callback);

}
