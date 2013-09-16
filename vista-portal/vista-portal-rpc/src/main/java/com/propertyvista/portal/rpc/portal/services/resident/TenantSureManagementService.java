/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureInsuranceStatusDTO;

public interface TenantSureManagementService extends IService {

    void getPreAuthorizedPaymentsAgreement(AsyncCallback<String> areementHtml);

    void getFaq(AsyncCallback<String> faqHtml);

    void getStatus(AsyncCallback<TenantSureInsuranceStatusDTO> callback);

    void updatePaymentMethod(AsyncCallback<VoidSerializable> callback, InsurancePaymentMethod paymentMethod);

    void cancelTenantSure(AsyncCallback<VoidSerializable> callback);

    void reinstate(AsyncCallback<VoidSerializable> callback);

    /**
     * if email is <code>null</code> will send the email to the tenant's email address, returns an email address that was used to send it.
     */
    void sendCertificate(AsyncCallback<String> defaultAsyncCallback, String email);

}
