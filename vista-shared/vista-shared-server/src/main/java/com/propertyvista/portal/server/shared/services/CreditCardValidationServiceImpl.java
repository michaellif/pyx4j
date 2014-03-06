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
package com.propertyvista.portal.server.shared.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.portal.rpc.shared.services.CreditCardValidationService;

public class CreditCardValidationServiceImpl implements CreditCardValidationService {

    private final static Logger log = LoggerFactory.getLogger(CreditCardValidationServiceImpl.class);

    @Override
    public void validate(AsyncCallback<Boolean> callback, CreditCardInfo creditCardInfo) {
        creditCardInfo.card().number().setValue(creditCardInfo.card().newNumber().getValue());
        boolean valid = ServerSideFactory.create(CreditCardFacade.class).validateVisaDebit(creditCardInfo);
        if (valid) {
            log.debug("card {} valid {}", creditCardInfo.cardType(), valid);
        } else {
            log.debug("card {} {}XXX not valid {}", creditCardInfo.cardType(), trim6(creditCardInfo.card().number().getValue()), valid);
        }
        callback.onSuccess(valid);
    }

    private String trim6(String value) {
        if (value.length() < 12) {
            return "short number";
        } else {
            return value.substring(0, 6);
        }
    }

}
