/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.server;

import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.ResponseMessage;

public class PaymentProcessor {

    public static ResponseMessage execute(RequestMessage request) {
        ResponseMessage response = new ResponseMessage();
        response.setMessageID(request.getMessageID());
        response.setMerchantId(request.getMerchantId());

        response.setStatus(ResponseMessage.StatusCode.OK);
        return response;
    }

}
