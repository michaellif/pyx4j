/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.rs;

import java.util.List;

import javax.ws.rs.Path;

import com.propertyvista.oapi.model.PaymentRecordIO;
import com.propertyvista.oapi.model.TransactionIO;

/**
 * Implementation of {@link RSReceivableService}
 * 
 */
@Path("/payments")
public class RSReceivableServiceImpl implements RSReceivableService {

    @Override
    public void postTransactions(List<TransactionIO> transactions) {
        // TODO Auto-generated method stub

    }

    @Override
    public void runBilling(String buildingCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reconcilePaymentRecords(List<PaymentRecordIO> records) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<PaymentRecordIO> getNonProcessedPaymentRecords() {
        // TODO Auto-generated method stub
        return null;
    }

}
