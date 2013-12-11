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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.CreditCardPaymentProcessorFacade;
import com.propertyvista.payment.FeeCalulationRequest;
import com.propertyvista.payment.FeeCalulationResponse;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentInstrument;
import com.propertyvista.payment.PaymentProcessingException;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;
import com.propertyvista.payment.caledon.dev.VisaDebitInternalValidator;

public class CaledonPaymentProcessor implements CreditCardPaymentProcessorFacade {

    private final static Logger log = LoggerFactory.getLogger(CaledonPaymentProcessor.class);

    private static final Set<String> networkErrorCodes = initNetworkErrorCodes();

    private CaledonHttpClient caledonCardsClient() {
        return new CaledonHttpClient();
    }

    private CaledonHttpClientFee caledonConvenienceFeeClient() {
        return new CaledonHttpClientFee();
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

    private CaledonCardProduct toCardProduct(CreditCardType creditCardType) {
        switch (creditCardType) {
        case MasterCard:
            return CaledonCardProduct.MasterCardCredit;
        case Visa:
            return CaledonCardProduct.VisaCredit;
        case VisaDebit:
            return CaledonCardProduct.VisaDebit;
        default:
            throw new PaymentProcessingException("Unknown CardType " + creditCardType);
        }
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
        if (request.convenienceFee().isNull() && request.convenienceFeeReferenceNumber().isNull()) {
            return realTimeSaleSimple(merchant, request);
        } else {
            return realTimeSaleConvenienceFee(merchant, request);
        }
    }

    private PaymentResponse realTimeSaleSimple(Merchant merchant, PaymentRequest request) throws PaymentProcessingException {
        CaledonRequest crequest = createRequestInstrument(request.paymentInstrument().<PaymentInstrument> cast());

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.SALE.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

        return createResponse(cresponse);
    }

    private PaymentResponse realTimeSaleConvenienceFee(Merchant merchant, PaymentRequest request) throws PaymentProcessingException {
        CaledonPaymentWithFeeRequest crequest = new CaledonPaymentWithFeeRequest();

        crequest.type = CaledonFeeRequestTypes.PaymentWithFee.getIntrfaceValue();
        crequest.terminalID = merchant.terminalID().getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();
        crequest.referenceNumberFeeCalulation = request.convenienceFeeReferenceNumber().getValue();
        crequest.setAmount(request.amount().getValue());
        crequest.setFeeAmount(request.convenienceFee().getValue());
        crequest.setTotalAmount(request.amount().getValue().add(request.convenienceFee().getValue()));
        crequest.setRecurring(false);

        crequest.setCardProduct(toCardProduct(request.paymentInstrument().cardType().getValue()));
        if (request.paymentInstrument().isInstanceOf(Token.class)) {
            crequest.token = request.paymentInstrument().<Token> cast().code().getValue();
        } else {
            throw new PaymentProcessingException("Unknown Payment Instrument " + request.paymentInstrument().getClass());
        }

        CaledonPaymentWithFeeResponse cresponse = caledonConvenienceFeeClient().transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.success().setValue("0000".equals(cresponse.responseCode));
        response.code().setValue(cresponse.responseCode);
        response.message().setValue(cresponse.responsePaymentAuthorization);

        if (response.success().getValue()) {
            response.authorizationNumber().setValue(cresponse.responsePaymentAuthorization + " " + cresponse.responseFeeAuthorization);

            //Validate the values returned by caledon
            if ((cresponse.terminalID == null) || (merchant.terminalID().getValue().compareTo(cresponse.terminalID) != 0)) {
                log.error("Erred Card transaction {} {} {} {}; response {}", //
                        merchant.terminalID(), //
                        request.referenceNumber(), //
                        request.amount(), //
                        request.convenienceFee(), //
                        cresponse.responseBody);
                throw new PaymentProcessingException("Protocol error, returned terminalID does not match");
            }
            if ((cresponse.referenceNumber == null) || (request.referenceNumber().getValue().compareTo(cresponse.referenceNumber) != 0)) {
                log.error("Erred Card transaction {} {} {} {}; response {}", //
                        merchant.terminalID(), //
                        request.referenceNumber(), //
                        request.amount(), //
                        request.convenienceFee(), //
                        cresponse.responseBody);
                throw new PaymentProcessingException("Protocol error, returned referenceNumber does not match");
            }
            if ((cresponse.getFeeAmount() == null) || (request.convenienceFee().getValue().compareTo(cresponse.getFeeAmount()) != 0)) {
                log.error("Erred Card transaction {} {} {} {}; response {}", //
                        merchant.terminalID(), //
                        request.referenceNumber(), //
                        request.amount(), //
                        request.convenienceFee(), //
                        cresponse.responseBody);
                throw new PaymentProcessingException("Protocol error, returned fee amount does not match");
            }
            if ((cresponse.getAmount() == null) || (request.amount().getValue().compareTo(cresponse.getAmount()) != 0)) {
                log.error("Erred Card transaction {} {} {} {}; response {}", //
                        merchant.terminalID(), //
                        request.referenceNumber(), //
                        request.amount(), //
                        request.convenienceFee(), //
                        cresponse.responseBody);
                throw new PaymentProcessingException("Protocol error, returned amount does not match");
            }
        }

        return response;
    }

    @Override
    public PaymentResponse realTimePreAuthorization(Merchant merchant, PaymentRequest request) {
        CaledonRequest crequest = createRequestInstrument(request.paymentInstrument().<PaymentInstrument> cast());

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.PREAUTH.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse realTimePreAuthorizationReversal(Merchant merchant, PaymentRequest request) {
        CaledonRequest crequest = createRequestInstrument(request.paymentInstrument().<PaymentInstrument> cast());

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.AUTH_REVERSE.getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();

        crequest.setAmount(BigDecimal.ZERO);

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

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

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

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

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

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

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse deactivateToken(Merchant merchant, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.DEACTIVATE.getValue();

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse reactivateToken(Merchant merchant, Token token) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.TOKEN.getValue();
        crequest.token = token.code().getValue();
        crequest.tokenAction = CaledonTokenAction.REACTIVATE.getValue();

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

        return createResponse(cresponse);
    }

    @Override
    public PaymentResponse voidTransaction(Merchant merchant, PaymentRequest request) {
        if (request.convenienceFee().isNull() && request.convenienceFeeReferenceNumber().isNull()) {
            return voidTransactionSimple(merchant, request);
        } else {
            return voidTransactionConvenienceFee(merchant, request);
        }
    }

    private PaymentResponse voidTransactionSimple(Merchant merchant, PaymentRequest request) {
        CaledonRequestToken crequest = new CaledonRequestToken();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.transactionType = CaledonTransactionType.VOID.getValue();
        crequest.creditCardNumber = "0";
        crequest.expiryDate = "0000";
        crequest.referenceNumber = request.referenceNumber().getValue();
        crequest.setAmount(request.amount().getValue());

        CaledonResponse cresponse = caledonCardsClient().transaction(crequest);

        return createResponse(cresponse);
    }

    private PaymentResponse voidTransactionConvenienceFee(Merchant merchant, PaymentRequest request) {
        CaledonPaymentWithFeeRequest crequest = new CaledonPaymentWithFeeRequest();

        crequest.type = CaledonFeeRequestTypes.Void.getIntrfaceValue();
        crequest.terminalID = merchant.terminalID().getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();
        crequest.referenceNumberFeeCalulation = request.convenienceFeeReferenceNumber().getValue();

        crequest.creditCardNumber = "0";
        crequest.expiryDate = "0000";

        crequest.setAmount(request.amount().getValue());
        crequest.setFeeAmount(request.convenienceFee().getValue());
        crequest.setTotalAmount(request.amount().getValue().add(request.convenienceFee().getValue()));

        CaledonPaymentWithFeeResponse cresponse = caledonConvenienceFeeClient().transaction(crequest);

        PaymentResponse response = EntityFactory.create(PaymentResponse.class);
        response.success().setValue("0000".equals(cresponse.responseCode));
        response.code().setValue(cresponse.responseCode);
        response.message().setValue(cresponse.responsePaymentAuthorization);

        return response;
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
        CaledonFeeCalulationRequest crequest = new CaledonFeeCalulationRequest();

        crequest.terminalID = merchant.terminalID().getValue();
        crequest.referenceNumber = request.referenceNumber().getValue();
        crequest.setAmount(request.amount().getValue());
        crequest.setCardProduct(toCardProduct(request.cardType().getValue()));

        CaledonFeeCalulationResponse cresponse = caledonConvenienceFeeClient().transaction(crequest);

        FeeCalulationResponse response = EntityFactory.create(FeeCalulationResponse.class);
        response.success().setValue("0000".equals(cresponse.responseCode));
        response.code().setValue(cresponse.responseCode);
        response.message().setValue(cresponse.responseText);

        if (response.success().getValue()) {
            if ((cresponse.getFeeAmount() == null) || (cresponse.getTotalAmount() == null) || (cresponse.getAmount() == null)) {
                log.error("Erred Card transaction {} {} {}; response {}", //
                        merchant.terminalID(), //
                        request.referenceNumber(), //
                        request.amount(), //
                        cresponse.responseBody);
                throw new PaymentProcessingException("Protocol error, amounts are not returned");
            }
            if (request.amount().getValue().compareTo(cresponse.getAmount()) != 0) {
                log.error("Erred Card transaction {} {} {}; response {}", //
                        merchant.terminalID(), //
                        request.referenceNumber(), //
                        request.amount(), //
                        cresponse.responseBody);
                throw new PaymentProcessingException("Protocol error, returned amount are do not match");
            }

            response.feeAmount().setValue(cresponse.getFeeAmount());
            response.totalAmount().setValue(cresponse.getTotalAmount());

            if (response.feeAmount().getValue().add(cresponse.getAmount()).compareTo(response.totalAmount().getValue()) != 0) {
                log.error("Erred Card transaction {} {} {}; response {}", //
                        merchant.terminalID(), //
                        request.referenceNumber(), //
                        request.amount(), //
                        cresponse.responseBody);
                throw new PaymentProcessingException("Protocol error, returned amounts total does not match");
            }
        }
        return response;
    }
}
