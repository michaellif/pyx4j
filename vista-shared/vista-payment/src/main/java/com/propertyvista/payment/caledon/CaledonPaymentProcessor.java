/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import java.math.BigDecimal;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentInstrument;
import com.propertyvista.payment.PaymentProcessingException;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

public class CaledonPaymentProcessor implements IPaymentProcessor {

    private final CaledonHttpClient client;

    public CaledonPaymentProcessor() {
        client = new CaledonHttpClient();
    }

    private CaledonRequest createRequestInstrument(PaymentInstrument paymentInstrument) {
        if (paymentInstrument instanceof CCInformation) {
            CaledonRequest crequest = new CaledonRequest();
            crequest.creditCardNumber = ((CCInformation) paymentInstrument).creditCardNumber().getValue();
            crequest.setExpiryDate(((CCInformation) paymentInstrument).creditCardExpiryDate().getValue());
            crequest.cvv = ((CCInformation) paymentInstrument).securityCode().getValue();
            return crequest;
        } else if (paymentInstrument instanceof Token) {
            CaledonRequestToken trequest = new CaledonRequestToken();
            trequest.token = ((Token) paymentInstrument).code().getValue();
            return trequest;
        } else {
            throw new PaymentProcessingException("Unknown Payment Instrument " + paymentInstrument.getClass());
        }
    }

    private PaymentResponse createResponse(CaledonResponse cresponse) {
        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.code().setValue(cresponse.code);
        response.message().setValue(cresponse.text);
        response.authorizationNumber().setValue(cresponse.authorizationNumber);
        return response;
    }

    @Override
    public PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request) throws PaymentProcessingException {
        CaledonRequest crequest = createRequestInstrument(request.paymentInstrument().<PaymentInstrument> cast());

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.SALE.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse realTimePreAuthorization(Merchant merchant, PaymentRequest request) {
        CaledonRequest crequest = createRequestInstrument(request.paymentInstrument().<PaymentInstrument> cast());

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.PREAUTH.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse realTimePreAuthorizationReversal(Merchant merchant, PaymentRequest request) {
        CaledonRequest crequest = createRequestInstrument(request.paymentInstrument().<PaymentInstrument> cast());

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.AUTH_REVERSE.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(BigDecimal.ZERO);

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse realTimePreAuthorizationCompletion(Merchant merchant, PaymentRequest request) {
        CaledonRequest crequest = createRequestInstrument(request.paymentInstrument().<PaymentInstrument> cast());

        if (!(crequest instanceof CaledonRequestToken)) {
            if (CommonsStringUtils.isEmpty(crequest.creditCardNumber)) {
                crequest.creditCardNumber = "0";
            }
        }

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.COMPLETION.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse createToken(Merchant merchant, CCInformation ccinfo, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.ADD.getValue();
        crequest.creditCardNumber = ccinfo.creditCardNumber().getValue();
        crequest.setExpiryDate(ccinfo.creditCardExpiryDate().getValue());
        crequest.cvv = ccinfo.securityCode().getValue();
        crequest.tokenRef = token.description().getValue();

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse updateToken(Merchant merchant, CCInformation ccinfo, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.UPDATE.getValue();
        crequest.creditCardNumber = ccinfo.creditCardNumber().getValue();
        crequest.setExpiryDate(ccinfo.creditCardExpiryDate().getValue());
        crequest.cvv = ccinfo.securityCode().getValue();
        crequest.tokenRef = token.description().getValue();

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse deactivateToken(Merchant merchant, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.DEACTIVATE.getValue();

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse reactivateToken(Merchant merchant, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.REACTIVATE.getValue();

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse tokenSale(Merchant merchant, PaymentRequest request) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.SALE.getValue();
        crequest.token = ((Token) (request.paymentInstrument().getValue())).code().getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();
        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

}
