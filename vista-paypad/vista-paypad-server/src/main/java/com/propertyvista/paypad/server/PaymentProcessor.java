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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.interfaces.payment.CreditCardInfo;
import com.propertyvista.interfaces.payment.Request;
import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.Response;
import com.propertyvista.interfaces.payment.ResponseMessage;
import com.propertyvista.interfaces.payment.TokenActionRequest;
import com.propertyvista.interfaces.payment.TokenActionRequest.TokenAction;
import com.propertyvista.interfaces.payment.TokenPaymentInstrument;
import com.propertyvista.interfaces.payment.TransactionRequest;
import com.propertyvista.payment.caledon.CaledonHttpClient;
import com.propertyvista.payment.caledon.CaledonRequest;
import com.propertyvista.payment.caledon.CaledonRequestToken;
import com.propertyvista.payment.caledon.CaledonResponse;
import com.propertyvista.payment.caledon.CaledonTokenAction;
import com.propertyvista.payment.caledon.CaledonTransactionType;

public class PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

    public static ResponseMessage execute(RequestMessage requestMessage) {
        ResponseMessage response = new ResponseMessage();
        response.setMessageID(requestMessage.getMessageID());
        response.setMerchantId(requestMessage.getMerchantId());

        for (Request request : requestMessage.getRequests()) {
            response.addResponse(execute(requestMessage, request));
        }

        response.setStatus(ResponseMessage.StatusCode.OK);
        return response;
    }

    private static Response execute(RequestMessage requestMessage, Request request) {
        Response response = new Response();
        response.setRequestID(request.getRequestID());
        response.setEcho(request.getEcho());

        try {
            if (request instanceof TokenActionRequest) {
                executeTokenActionRequest(requestMessage, (TokenActionRequest) request, response);
            } else if (request instanceof TransactionRequest) {
                executeTransactionRequest(requestMessage, (TransactionRequest) request, response);
            } else {
                setErrorCode(response, "7000", "MALFORMED REQUEST");
            }
        } catch (Throwable e) {
            log.error("transaction error", e);
            setErrorCode(response, "7001", "INTERNAL ERROR");
        }

        return response;
    }

    static void setErrorCode(Response response, String code, String text) {
        response.setCode(code);
        response.setText(text);
    }

    private static void executeTokenActionRequest(RequestMessage requestMessage, TokenActionRequest tokenActionRequest, Response response) {
        CaledonHttpClient client = new CaledonHttpClient();
        CaledonRequestToken crequest = new CaledonRequestToken();
        crequest.terminalID = requestMessage.getMerchantId();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = tokenActionRequest.getCode();

        switch (tokenActionRequest.getAction()) {
        case Add:
            crequest.tokenAction = CaledonTokenAction.ADD.getValue();
            break;
        case Update:
            crequest.tokenAction = CaledonTokenAction.UPDATE.getValue();
            break;
        case Deactivate:
            crequest.tokenAction = CaledonTokenAction.DEACTIVATE.getValue();
            break;
        case Reactivate:
            crequest.tokenAction = CaledonTokenAction.REACTIVATE.getValue();
            break;
        default:
            setErrorCode(response, "7000", "MALFORMED REQUEST");
            return;
        }

        if ((tokenActionRequest.getAction() == TokenAction.Add) || (tokenActionRequest.getAction() == TokenAction.Update)) {
            crequest.creditCardNumber = tokenActionRequest.getCard().getCardNumber();
            crequest.setExpiryDate(tokenActionRequest.getCard().getExpiryDate());
            crequest.tokenRef = tokenActionRequest.getReference();
        }

        CaledonResponse cresponse = client.transaction(crequest);

        response.setCode(cresponse.code);
        response.setText(cresponse.text);
    }

    private static void executeTransactionRequest(RequestMessage requestMessage, TransactionRequest transactionRequest, Response response) {
        CaledonHttpClient client = new CaledonHttpClient();

        CaledonRequest crequest = new CaledonRequest();
        crequest.terminalID = requestMessage.getMerchantId();

        if (transactionRequest.getPaymentInstrument() instanceof TokenPaymentInstrument) {
            crequest.token = ((TokenPaymentInstrument) transactionRequest.getPaymentInstrument()).getCode();
        } else if (transactionRequest.getPaymentInstrument() instanceof CreditCardInfo) {
            crequest.creditCardNumber = ((CreditCardInfo) transactionRequest.getPaymentInstrument()).getCardNumber();
            crequest.setExpiryDate(((CreditCardInfo) transactionRequest.getPaymentInstrument()).getExpiryDate());
        } else {
            setErrorCode(response, "7000", "MALFORMED REQUEST");
            return;
        }

        crequest.setAmount(transactionRequest.getAmount());
        crequest.referenceNumber = transactionRequest.getReference();

        switch (transactionRequest.getTxnType()) {
        case Sale:
            crequest.transactionType = CaledonTransactionType.SALE.getValue();
            break;
        case AuthorizeOnly:
            crequest.transactionType = CaledonTransactionType.AUTH_ONLY.getValue();
            break;
        case AuthReversal:
            crequest.transactionType = CaledonTransactionType.AUTH_REVERSE.getValue();
            break;
        case Completion:
            crequest.transactionType = CaledonTransactionType.COMPLETION.getValue();
            break;
        case PreAuthorization:
            crequest.transactionType = CaledonTransactionType.PREAUTH.getValue();
            break;
        case Return:
            crequest.transactionType = CaledonTransactionType.RETURN.getValue();
            break;
        default:
            setErrorCode(response, "7000", "MALFORMED REQUEST");
            return;
        }

        CaledonResponse cresponse = client.transaction(crequest);

        response.setCode(cresponse.code);
        response.setText(cresponse.text);
        response.setAuth(cresponse.authorizationNumber);

    }
}
