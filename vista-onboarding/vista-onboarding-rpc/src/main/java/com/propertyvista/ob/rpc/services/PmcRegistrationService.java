/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 */
package com.propertyvista.ob.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.ob.rpc.dto.OnboardingCrmURL;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;

public interface PmcRegistrationService extends IService {

    //Returns deferredCorrelationId
    public void createAccount(AsyncCallback<String> callback, PmcAccountCreationRequest request);

    public void obtainCrmURL(AsyncCallback<OnboardingCrmURL> callback);
}
