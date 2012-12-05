/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.billingext.ExternalBillingFacade;
import com.propertyvista.domain.financial.billingext.dto.ChargeDTO;
import com.propertyvista.oapi.marshaling.ChargeMarshaller;
import com.propertyvista.oapi.model.ChargeIO;
import com.propertyvista.oapi.model.TransactionIO;

public class ReceivableService {

    public static void postTransaction(TransactionIO transaction) {
        if (transaction instanceof ChargeIO) {
            ChargeIO chargeIO = (ChargeIO) transaction;
            ChargeDTO chargeDTO = ChargeMarshaller.getInstance().unmarshal(chargeIO);

            ServerSideFactory.create(ExternalBillingFacade.class).postCharge(chargeDTO, chargeIO.leaseId);
        }
    }
}
