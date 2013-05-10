/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.ws;

import java.util.List;

import javax.jws.WebService;

import com.propertyvista.oapi.ReceivableService;
import com.propertyvista.oapi.model.PaymentRecordIO;
import com.propertyvista.oapi.model.TransactionIO;

@WebService(endpointInterface = "com.propertyvista.oapi.ws.WSReceivableService")
public class WSReceivableServiceImpl implements WSReceivableService {

    @Override
    public void postTransactions(List<TransactionIO> transactions) {
        for (TransactionIO transIO : transactions) {
            ReceivableService.postTransaction(transIO);
        }
    }

    @Override
    public void runBilling(String buildingCode) {
        ReceivableService.runBilling(buildingCode);
    }

    @Override
    public List<PaymentRecordIO> getNonProcessedPaymentRecords() {
        return ReceivableService.getNonProcessedPaymentRecords();
    }

    @Override
    public void reconcilePaymentRecords(List<PaymentRecordIO> records) {
        ReceivableService.reconcilePaymentRecords(records);
    }

}
