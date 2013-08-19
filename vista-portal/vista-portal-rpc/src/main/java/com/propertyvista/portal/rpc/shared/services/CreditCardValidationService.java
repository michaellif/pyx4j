/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.payment.CreditCardInfo;

public interface CreditCardValidationService extends IService {

    /**
     * For now only VisaDebit cards should be validated this way.
     * 
     * @param callback
     * @param creditCardInfo
     *            creditCardInfo.card().newNumber() should be populated
     */
    public void validate(AsyncCallback<Boolean> callback, CreditCardInfo creditCardInfo);

}
