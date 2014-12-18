/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-03
 * @author vlads
 */
package com.propertyvista.crm.rpc.services.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.payment.EcheckInfo;

public interface RevealAccountNumberService extends IService {

    //The accountNo.newNumber() value will be set in return. 
    void obtainUnobfuscatedAccountNumber(AsyncCallback<EcheckInfo> callback, EcheckInfo echeckInfoEntityId);
}
