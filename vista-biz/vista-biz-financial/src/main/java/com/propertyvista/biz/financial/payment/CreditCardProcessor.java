/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.dto.payment.ConvenienceFeeCalulationResponseTO;
import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.FeeCalulationRequest;
import com.propertyvista.payment.FeeCalulationResponse;
import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentInstrument;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;
import com.propertyvista.payment.caledon.CaledonPaymentProcessor;

class CreditCardProcessor {

    private final static Logger log = LoggerFactory.getLogger(CreditCardProcessor.class);

    private static final I18n i18n = I18n.get(CreditCardProcessor.class);

    static interface MerchantTerminalSource {

        String getMerchantTerminalId();

    }

    static class MerchantTerminalSourceBuilding implements MerchantTerminalSource {

        Building building;

        public MerchantTerminalSourceBuilding(Building building) {
            this.building = building;
        }

        @Override
        public String getMerchantTerminalId() {
            MerchantAccount account = PaymentUtils.retrieveMerchantAccount(building);
            return account.merchantTerminalId().getValue();
        }

    }

    static class MerchantTerminalSourceConst implements MerchantTerminalSource {

        private final String merchantTerminalId;

        public MerchantTerminalSourceConst(String merchantTerminalId) {
            super();
            this.merchantTerminalId = merchantTerminalId;
        }

        @Override
        public String getMerchantTerminalId() {
            return merchantTerminalId;
        }
    }

    static IPaymentProcessor getPaymentProcessor() {
        return new CaledonPaymentProcessor();
    }

    static void persistToken(String merchantTerminalId, CreditCardInfo cc) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        CCInformation ccInfo = EntityFactory.create(CCInformation.class);
        if (!cc.card().number().isNull()) {
            if (!ValidationUtils.isCreditCardNumberValid(cc.card().number().getValue())) {
                throw new UserRuntimeException(i18n.tr("Invalid Credit Card Number"));
            }
            if (!ValidationUtils.isCreditCardNumberIinValid(cc.cardType().getValue().iinsPatterns, cc.card().number().getValue())) {
                throw new UserRuntimeException(i18n.tr("The credit card number doesn't match the credit card type"));
            }
            if ((cc.cardType().getValue() == CreditCardType.VisaDebit) && (!validateVisaDebit(cc))) {
                throw new UserRuntimeException(i18n.tr("The credit card number doesn't match the credit card type"));
            }
            ccInfo.creditCardNumber().setValue(cc.card().number().getValue());
        }
        ccInfo.creditCardExpiryDate().setValue(cc.expiryDate().getValue());
        ccInfo.securityCode().setValue(cc.securityCode().getValue());

        if (cc.id().isNull()) {
            throw new Error("CreditCardInfo should be saved first");
        }

        Token token = EntityFactory.create(Token.class);
        if (!cc.token().isNull()) {
            token.code().setValue(cc.token().getValue());
        } else {
            Validate.isTrue(!ccInfo.creditCardNumber().isNull(), "Card number is required when creating token");
            //Create Unique token using PMC Id
            String prefix = "";

            if (VistaDeployment.isSystemNamespace()) {
                prefix = "v2p";
            } else {
                Pmc pmc = VistaDeployment.getCurrentPmc();
                prefix += pmc.id().getStringView();
            }

            if (VistaDeployment.isVistaProduction()) {
                prefix += "";
            } else {
                prefix += "TEST" + new SimpleDateFormat("MMddHHmmss").format(new Date());
                prefix += "r" + new Random().nextInt(100);
            }
            token.code().setValue(prefix + "V" + cc.id().getStringView());
        }

        PaymentResponse response;
        if (!cc.token().isNull()) {
            response = getPaymentProcessor().updateToken(merchant, ccInfo, token);
        } else {
            response = getPaymentProcessor().createToken(merchant, ccInfo, token);
        }

        if (response.success().getValue(false)) {
            cc.token().setValue(token.code().getValue());
        } else if (response.code().getValue("").equals("1019")) {
            log.error("Response Message {}, Code {} ;  Merchant account is not setup {}", response.message(), response.code(), merchantTerminalId);
            throw new UserRuntimeException(i18n.tr("Merchant account is not setup to receive CreditCard Payments"));
        } else if (response.code().getValue("").equals("1001")) {
            log.error("Response Message {}, Code {} ;  Merchant account is not activated {}", response.message(), response.code(), merchantTerminalId);
            throw new UserRuntimeException(i18n.tr("Merchant account is not activated"));
        } else {
            throw new UserRuntimeException(response.message().getValue());
        }

    }

    static boolean validateVisaDebit(CreditCardInfo cc) {
        if (cc.cardType().getValue() != CreditCardType.VisaDebit) {
            return false;
        } else {
            CCInformation ccInfo = EntityFactory.create(CCInformation.class);
            ccInfo.creditCardNumber().setValue(cc.card().number().getValue());

            PaymentResponse response = getPaymentProcessor().validateVisaDebit(ccInfo);
            return response.success().getValue();
        }
    }

    private static PaymentInstrument createPaymentInstrument(CreditCardInfo cc) {
        if (!cc.token().isNull()) {
            Token token = EntityFactory.create(Token.class);
            token.code().setValue(cc.token().getStringView());
            token.cardType().setValue(cc.cardType().getValue());
            return token;
        } else {
            if (!ValidationUtils.isCreditCardNumberValid(cc.card().number().getValue())) {
                throw new UserRuntimeException(i18n.tr("Invalid Credit Card Number"));
            }
            if (!ValidationUtils.isCreditCardNumberIinValid(cc.cardType().getValue().iinsPatterns, cc.card().number().getValue())) {
                throw new UserRuntimeException(i18n.tr("The credit card number doesn't match the credit card type"));
            }
            if ((cc.cardType().getValue() == CreditCardType.VisaDebit) && (!validateVisaDebit(cc))) {
                throw new UserRuntimeException(i18n.tr("The credit card number doesn't match the credit card type"));
            }
            CCInformation ccInfo = EntityFactory.create(CCInformation.class);
            ccInfo.creditCardNumber().setValue(cc.card().number().getValue());
            ccInfo.creditCardExpiryDate().setValue(cc.expiryDate().getValue());
            ccInfo.securityCode().setValue(cc.securityCode().getValue());
            ccInfo.cardType().setValue(cc.cardType().getValue());
            return ccInfo;
        }
    }

    static CreditCardTransactionResponse realTimeSale(String merchantTerminalId, BigDecimal amount, BigDecimal convenienceFee, String referenceNumber,
            CreditCardInfo cc) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.referenceNumber().setValue(referenceNumber);
        request.amount().setValue(amount);
        request.convenienceFee().setValue(convenienceFee);

        request.paymentInstrument().set(createPaymentInstrument(cc));

        PaymentResponse response = getPaymentProcessor().realTimeSale(merchant, request);
        if (response.success().getValue()) {
            log.debug("ccPayment transaction accepted {}", response);
        } else {
            log.debug("ccPayment transaction rejected {}", response);
        }
        return createResponse(response);
    }

    static CreditCardTransactionResponse voidTransaction(String merchantTerminalId, BigDecimal amount, String referenceNumber) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.referenceNumber().setValue(referenceNumber);
        request.amount().setValue(amount);

        PaymentResponse response = getPaymentProcessor().voidTransaction(merchant, request);
        if (response.success().getValue()) {
            log.debug("ccPayment transaction accepted {}", response);
        } else {
            log.debug("ccPayment transaction rejected {}", response);
        }
        return createResponse(response);
    }

    private static CreditCardTransactionResponse createResponse(PaymentResponse cresponse) {
        CreditCardTransactionResponse response = EntityFactory.create(CreditCardTransactionResponse.class);
        response.success().setValue(cresponse.success().getValue());
        response.code().setValue(cresponse.code().getValue());
        response.message().setValue(cresponse.message().getValue());
        response.authorizationNumber().setValue(cresponse.authorizationNumber().getValue());
        return response;
    }

    static String preAuthorization(String merchantTerminalId, BigDecimal amount, String referenceNumber, CreditCardInfo cc) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.referenceNumber().setValue(referenceNumber);
        request.amount().setValue(amount);

        request.paymentInstrument().set(createPaymentInstrument(cc));

        PaymentResponse response = getPaymentProcessor().realTimePreAuthorization(merchant, request);
        if (response.success().getValue()) {
            log.debug("ccTransaction accepted {}", response);
            return response.authorizationNumber().getValue();
        } else {
            log.debug("ccTransaction rejected {}", response);
            throw new UserRuntimeException(i18n.tr("Credit Card Authorization failed {0}", response.message()));
        }
    }

    static void preAuthorizationReversal(String merchantTerminalId, String referenceNumber, CreditCardInfo cc) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.referenceNumber().setValue(referenceNumber);

        request.paymentInstrument().set(createPaymentInstrument(cc));

        PaymentResponse response = getPaymentProcessor().realTimePreAuthorizationReversal(merchant, request);
        if (response.success().getValue()) {
            log.debug("ccTransaction accepted {}", response);
        } else {
            log.debug("ccTransaction rejected {}", response);
            throw new UserRuntimeException(i18n.tr("Credit Card Pre-authorization reversal failed {0}", response.message()));
        }
    }

    static String completion(String merchantTerminalId, BigDecimal amount, String referenceNumber, CreditCardInfo cc) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.referenceNumber().setValue(referenceNumber);
        request.amount().setValue(amount);

        request.paymentInstrument().set(createPaymentInstrument(cc));

        PaymentResponse response = getPaymentProcessor().realTimePreAuthorizationCompletion(merchant, request);
        if (response.success().getValue()) {
            log.debug("ccTransaction accepted {}", response);
            return response.authorizationNumber().getValue();
        } else {
            log.debug("ccTransaction rejected {}", response);
            throw new UserRuntimeException(i18n.tr("Credit Card Payment failed {0}", response.message()));
        }
    }

    public static ConvenienceFeeCalulationResponseTO getConvenienceFee(String merchantTerminalId, CreditCardType cardType, BigDecimal amount) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        //TODO
        String referenceNumber = "TODO1";

        FeeCalulationRequest request = EntityFactory.create(FeeCalulationRequest.class);
        request.amount().setValue(amount);
        request.cardType().setValue(cardType);

        request.referenceNumber().setValue(referenceNumber);

        FeeCalulationResponse response = getPaymentProcessor().getConvenienceFee(merchant, request);

        if (response.success().getValue()) {
            log.debug("fee calulatedd {}", response);

            ConvenienceFeeCalulationResponseTO to = EntityFactory.create(ConvenienceFeeCalulationResponseTO.class);
            to.transactionNumber().setValue(referenceNumber);
            to.amount().setValue(amount);
            to.feeAmount().setValue(response.feeAmount().getValue());
            return to;
        } else {
            log.debug("cc Fee Calulation rejected {}", response);
            throw new UserRuntimeException(i18n.tr("Card Fee Calulation failed {0}", response.message()));
        }
    }
}
