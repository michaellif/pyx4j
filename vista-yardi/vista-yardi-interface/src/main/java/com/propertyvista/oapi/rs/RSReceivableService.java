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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.propertyvista.oapi.ReceivableService;
import com.propertyvista.oapi.model.PaymentRecordIO;
import com.propertyvista.oapi.model.TransactionIO;

/**
 * 
 * interfaces/oapi/rs/payments/postTransactions - post all transactions
 * 
 * interfaces/oapi/rs/payments/<buildingCode>/runBilling - runs billing for corresponding building
 * 
 * interfaces/oapi/rs/payments/nonProcessed - returns list of non processed payments
 * 
 * interfaces/oapi/rs/payments/reconcile - reconciles payments
 * 
 */

@Path("/payments")
public class RSReceivableService {

    @POST
    @Path("/transactions")
    @Consumes({ MediaType.APPLICATION_XML })
    public Response postTransactions(List<TransactionIO> transactions) {
        try {
            for (TransactionIO transIO : transactions) {
                ReceivableService.postTransaction(transIO);
            }
            return Response.ok().build();
        } catch (Exception e) {
            throw new RSServiceException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @POST
    @Path("/{propertyCode}/runBilling")
    public Response runBilling(@PathParam("propertyCode") String buildingCode) {
        try {
            ReceivableService.runBilling(buildingCode);
            return Response.ok().build();
        } catch (Exception e) {
            throw new RSServiceException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @POST
    @Path("/reconcile")
    @Consumes({ MediaType.APPLICATION_XML })
    public Response reconcilePaymentRecords(List<PaymentRecordIO> records) {
        try {
            ReceivableService.reconcilePaymentRecords(records);
            return Response.ok().build();
        } catch (Exception e) {
            throw new RSServiceException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @GET
    @Path("/nonProcessed")
    @Produces({ MediaType.APPLICATION_XML })
    public List<PaymentRecordIO> getNonProcessedPaymentRecords() {
        return ReceivableService.getNonProcessedPaymentRecords();
    }

}
