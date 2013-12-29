/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.cards;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.payment.PaymentResponse;

class PaymentResponseHelper {

    static PaymentResponse createResponse(String code, String message) {
        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.success().setValue("0000".equals(code));
        response.code().setValue(code);
        response.message().setValue(message);
        return response;
    }
}
