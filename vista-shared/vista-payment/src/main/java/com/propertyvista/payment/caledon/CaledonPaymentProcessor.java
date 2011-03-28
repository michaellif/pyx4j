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

import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentInstrument;
import com.propertyvista.payment.PaymentProcessingException;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

import com.pyx4j.entity.shared.EntityFactory;

public class CaledonPaymentProcessor implements IPaymentProcessor {

    private final CaledonHttpClient client;

    public CaledonPaymentProcessor() {
        client = new CaledonHttpClient();
    }

    @Override
    public PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request) throws PaymentProcessingException {
        CaledonRequest crequest = null;
        PaymentInstrument pinstrument = request.paymentInstrument().getValue();
        if (pinstrument.getClass().equals(CCInformation.class)) {
            crequest = new CaledonRequest();
            crequest.terminalID = merchant.terminalID().getValue();
            crequest.transactionType = CaledonTransactionType.SALE.getValue();
            crequest.referenceNumber = request.referenceNumber().getValue();

            crequest.setAmount(request.amount().getValue());
            crequest.creditCardNumber = ((CCInformation) pinstrument).creditCardNumber().getValue();
            //crequest.creditCardNumber = request.creditCardNumber().getValue();
            crequest.setExpiryDate(((CCInformation) (request.paymentInstrument().getValue())).creditCardExpiryDate().getValue());
            //crequest.setExpiryDate(request.creditCardExpiryDate().getValue());
        } else if (pinstrument.getClass().equals(Token.class)) {
            crequest = new CaledonRequestToken();
            crequest.terminalID = merchant.terminalID().getValue();
            crequest.transactionType = CaledonTransactionType.SALE.getValue();
            ((CaledonRequestToken) crequest).token = ((Token) pinstrument).code().getValue();
            crequest.referenceNumber = request.referenceNumber().getValue();
            crequest.setAmount(request.amount().getValue());
        } else
            throw new PaymentProcessingException("Unknown Payment Instrument.");

        CaledonResponse cresponse = client.transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.code().setValue(cresponse.code);
        response.message().setValue(cresponse.text);
        response.authorizationNumber().setValue(cresponse.authorizationNumber);

        return response;
    }

    @Override
    public PaymentResponse realTimeAuthorization(Merchant merchant, PaymentRequest request) {
        return null;
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
        crequest.tokenRef = token.description().getValue();
        //crequest.creditCardNumber = request.creditCardNumber().getValue();
        //crequest.setExpiryDate(request.creditCardExpiryDate().getValue());

        CaledonResponse cresponse = client.transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.code().setValue(cresponse.code);
        response.message().setValue(cresponse.text);
        response.authorizationNumber().setValue(cresponse.authorizationNumber);

        return response;
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
        crequest.tokenRef = token.description().getValue();

        CaledonResponse cresponse = client.transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.code().setValue(cresponse.code);
        response.message().setValue(cresponse.text);
        response.authorizationNumber().setValue(cresponse.authorizationNumber);

        return response;
    }

    @Override
    public PaymentResponse deactivateToken(Merchant merchant, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.DEACTIVATE.getValue();

        CaledonResponse cresponse = client.transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.code().setValue(cresponse.code);
        response.message().setValue(cresponse.text);
        response.authorizationNumber().setValue(cresponse.authorizationNumber);

        return response;
    }

    @Override
    public PaymentResponse reactivateToken(Merchant merchant, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.REACTIVATE.getValue();

        CaledonResponse cresponse = client.transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.code().setValue(cresponse.code);
        response.message().setValue(cresponse.text);
        response.authorizationNumber().setValue(cresponse.authorizationNumber);

        return response;
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

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.code().setValue(cresponse.code);
        response.message().setValue(cresponse.text);
        response.authorizationNumber().setValue(cresponse.authorizationNumber);

        return response;
    }

}
