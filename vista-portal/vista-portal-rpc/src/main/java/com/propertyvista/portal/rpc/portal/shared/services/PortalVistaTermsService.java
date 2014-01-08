/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

public interface PortalVistaTermsService extends IService {

    void getPortalTerms(AsyncCallback<String> callback);

    // ============== Resident ==============

    void getTenantBillingTerms(AsyncCallback<String> callback);

    void getTenantPreauthorizedPaymentTerms(AsyncCallback<String> callback);

    void getResidentPortalCcPolicy(AsyncCallback<String> callback);

    void getResidentPortalConvenienceFeeTerms(AsyncCallback<String> callback);

    void getTenantSurePreAuthorizedPaymentsAgreement(AsyncCallback<String> callback);

    // ============== Prospect ==============

    void getProspectApplicantTerms(AsyncCallback<String> callback);

    void getProspectRentalCriteriaGuidelines(AsyncCallback<String> callback);

}
