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
 */
package com.propertyvista.oapi.v1.ws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.propertyvista.oapi.v1.model.PaymentRecordIO;
import com.propertyvista.oapi.v1.model.TransactionIO;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface WSReceivableService {

    void postTransactions(@WebParam(name = "transaction") List<TransactionIO> transactions);

    void runBilling(String buildingCode);

    List<PaymentRecordIO> getNonProcessedPaymentRecords();

    void reconcilePaymentRecords(List<PaymentRecordIO> records);

}
