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
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.FeeCalulationRequest;
import com.propertyvista.payment.FeeCalulationResponse;
import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentInstrument;
import com.propertyvista.payment.PaymentProcessingException;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;
import com.propertyvista.payment.caledon.dev.VisaDebitInternalValidator;

public class CaledonPaymentProcessor implements IPaymentProcessor {

    private final CaledonHttpClient client;

    private static final Set<String> networkErrorCodes = initNetworkErrorCodes();

    public CaledonPaymentProcessor() {
        client = new CaledonHttpClient();
    }

    private static Set<String> initNetworkErrorCodes() {
        Set<String> codes = new HashSet<String>();
        codes.add("1023");
        codes.add("1024");
        codes.add("1025");
        codes.add("1088");
        codes.add("1099");
        codes.add("1106");
        return codes;
    }

    @Override
    public boolean isNetworkError(String responseCode) {
        return networkErrorCodes.contains(responseCode);
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
        response.success().setValue("0000".equals(cresponse.code));
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

    @Override
    public PaymentResponse voidTransaction(Merchant merchant, PaymentRequest request) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.VOID.getValue();
        crequest.creditCardNumber = "0";
        crequest.expiryDate = "0000";
        crequest.referenceNumber = request.referenceNumber().getValue();
        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = client.transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse validateVisaDebit(CCInformation ccinfo) {
        if (VisaDebitInternalValidator.isVisaDebitValid(ccinfo.creditCardNumber().getValue())) {
            PaymentResponse response = EntityFactory.create(PaymentResponse.class);
            response.success().setValue(true);
            response.code().setValue("0000");
            return response;
        } else {
            PaymentResponse response = EntityFactory.create(PaymentResponse.class);
            response.success().setValue(false);
            response.code().setValue("1020");
            response.message().setValue("The card number did not pass check-digit tests for that card type.");
            return response;
        }
    }

    @Override
    public FeeCalulationResponse getConvenienceFee(Merchant merchant, FeeCalulationRequest request) {
        // TODO Auto-generated method stub
        return null;
    }
}
