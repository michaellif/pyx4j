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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;
import com.propertyvista.payment.caledon.CaledonPaymentProcessor;
import com.propertyvista.shared.VistaSystemIdentification;

class CreditCardProcessor {

    static void realTimeSale(PaymentRecord paymentRecord) {
        MerchantAccount account = PaymentUtils.retrieveMerchantAccount(paymentRecord);

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
            CCInformation ccInfo = EntityFactory.create(CCInformation.class);
            ccInfo.creditCardNumber().setValue(cc.number().getValue());
            ccInfo.creditCardExpiryDate().setValue(cc.expiryDate().getValue());
            ccInfo.securityCode().setValue(cc.securityCode().getValue());

            request.paymentInstrument().set(ccInfo);
        }

        IPaymentProcessor proc = new CaledonPaymentProcessor();

        PaymentResponse response = proc.realTimeSale(merchant, request);
        if (response.code().getValue().equals("0000")) {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            paymentRecord.transactionAuthorizationNumber().setValue(response.authorizationNumber().getValue());
            Persistence.service().merge(paymentRecord);
            Persistence.service().commit();
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
        } else {
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
            paymentRecord.transactionAuthorizationNumber().setValue(response.code().getValue());
            paymentRecord.transactionErrorMessage().setValue(response.message().getValue());
            Persistence.service().merge(paymentRecord);
            Persistence.service().commit();
            ServerSideFactory.create(ARFacade.class).postPayment(paymentRecord);
        }
    }

    public static void persistToken(Building building, CreditCardInfo cc) {
        MerchantAccount account = PaymentUtils.retrieveMerchantAccount(building);
        Merchant merchant = EntityFactory.create(Merchant.class);
        merchant.terminalID().setValue(account.merchantTerminalId().getValue());

        CCInformation ccInfo = EntityFactory.create(CCInformation.class);
        ccInfo.creditCardNumber().setValue(cc.number().getValue());
        ccInfo.creditCardExpiryDate().setValue(cc.expiryDate().getValue());
        ccInfo.securityCode().setValue(cc.securityCode().getValue());

        Token token = EntityFactory.create(Token.class);
        if (!cc.token().isNull()) {
            token.code().setValue(cc.token().getValue());
        } else {
            //Create Unique token using PMC Id
            Pmc pmc = VistaDeployment.getCurrentPmc();
            String prefix;
            if ((!ApplicationMode.isDevelopment()) && (VistaSystemIdentification.production == VistaDeployment.getSystemIdentification())) {
                prefix = "";
            } else {
                prefix = "TEST" + new SimpleDateFormat("MMddHH").format(new Date());
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
        } else {
            throw new UserRuntimeException(response.message().getValue());
        }

    }
}
