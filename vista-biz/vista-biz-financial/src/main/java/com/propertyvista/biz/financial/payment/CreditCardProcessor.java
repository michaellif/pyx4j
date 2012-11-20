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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;
import com.propertyvista.payment.caledon.CaledonPaymentProcessor;
import com.propertyvista.server.jobs.TaskRunner;

class CreditCardProcessor {

    private final static Logger log = LoggerFactory.getLogger(CreditCardProcessor.class);

    private static final I18n i18n = I18n.get(CreditCardProcessor.class);

    static PaymentRecord realTimeSale(final PaymentRecord paymentRecord) {
        return TaskRunner.runAutonomousTransation(new Callable<PaymentRecord>() {
            @Override
            public PaymentRecord call() {
                Persistence.service().merge(paymentRecord);
                doRealTimeSale(paymentRecord);
                return paymentRecord;
            }
        });
    }

    private static void doRealTimeSale(PaymentRecord paymentRecord) {
        MerchantAccount account = PaymentUtils.retrieveValidMerchantAccount(paymentRecord);

        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(account.merchantTerminalId().getValue());

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        //TODO identify what should be there
        request.referenceNumber().setValue(paymentRecord.id().getStringView());
        request.amount().setValue(paymentRecord.amount().getValue().floatValue());
        CreditCardInfo cc = paymentRecord.paymentMethod().details().cast();
        if (!cc.token().isNull()) {
            Token token = EntityFactory.create(Token.class);
            token.code().setValue(cc.token().getStringView());
            request.paymentInstrument().set(token);
        } else {
            if (!ValidationUtils.isCreditCardNumberValid(cc.card().number().getValue())) {
                throw new UserRuntimeException(i18n.tr("Invalid Credit Card Number"));
            }
            CCInformation ccInfo = EntityFactory.create(CCInformation.class);
            ccInfo.creditCardNumber().setValue(cc.card().number().getValue());
            ccInfo.creditCardExpiryDate().setValue(cc.expiryDate().getValue());
            ccInfo.securityCode().setValue(cc.securityCode().getValue());

            request.paymentInstrument().set(ccInfo);
        }

        IPaymentProcessor proc = new CaledonPaymentProcessor();

        PaymentResponse response = proc.realTimeSale(merchant, request);
        if (response.code().getValue().equals("0000")) {
            log.debug("ccTransaction accepted {}", response);
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
            paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            paymentRecord.receivedDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            paymentRecord.transactionAuthorizationNumber().setValue(response.authorizationNumber().getValue());
            Persistence.service().merge(paymentRecord);
            Persistence.service().commit();
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
            Persistence.service().commit();
        } else {
            log.debug("ccTransaction rejected {}", response);
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
            paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            paymentRecord.transactionAuthorizationNumber().setValue(response.code().getValue());
            paymentRecord.transactionErrorMessage().setValue(response.message().getValue());
            Persistence.service().merge(paymentRecord);
            Persistence.service().commit();
        }
    }

    public static void persistToken(Building building, CreditCardInfo cc) {
        MerchantAccount account = PaymentUtils.retrieveMerchantAccount(building);
        persistToken(account.merchantTerminalId().getValue(), cc);
    }

    static void persistToken(String merchantTerminalId, CreditCardInfo cc) {
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(merchantTerminalId);

        CCInformation ccInfo = EntityFactory.create(CCInformation.class);
        if (!cc.card().number().isNull()) {
            if (!ValidationUtils.isCreditCardNumberValid(cc.card().number().getValue())) {
                throw new UserRuntimeException(i18n.tr("Invalid Credit Card Number"));
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
            Validate.isTrue(!ccInfo.creditCardNumber().isNull());
            //Create Unique token using PMC Id
            Pmc pmc = VistaDeployment.getCurrentPmc();
            String prefix;
            if (VistaDeployment.isVistaProduction()) {
                prefix = "";
            } else {
                prefix = "TEST" + new SimpleDateFormat("MMddHHmm").format(new Date());
            }
            token.code().setValue(prefix + pmc.id().getStringView() + "V" + cc.id().getStringView());
        }

        IPaymentProcessor proc = new CaledonPaymentProcessor();
        PaymentResponse response;
        if (!cc.token().isNull()) {
            response = proc.updateToken(merchant, ccInfo, token);
        } else {
            response = proc.createToken(merchant, ccInfo, token);
        }

        if (response.code().getValue().equals("0000")) {
            cc.token().setValue(token.code().getValue());
        } else if (response.code().getValue().equals("1019")) {
            throw new UserRuntimeException(i18n.tr("Merchant account is not setup to receive CreditCard Payments"));
        } else if (response.code().getValue().equals("1001")) {
            throw new UserRuntimeException(i18n.tr("Merchant account is not activated"));
        } else {
            throw new UserRuntimeException(response.message().getValue());
        }

    }
}
