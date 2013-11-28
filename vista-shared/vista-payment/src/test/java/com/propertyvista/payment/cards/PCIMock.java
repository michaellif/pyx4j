/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.cards;

import static com.propertyvista.payment.cards.PaymentResponseHelper.createResponse;

import java.util.HashMap;
import java.util.Map;

import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentInstrument;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

class PCIMock {

    private static class SingletonHolder {
        public static final PCIMock INSTANCE = new PCIMock();
    }

    static PCIMock instance() {
        return SingletonHolder.INSTANCE;
    }

    private final Map<String, CardAccountMock> accounts = new HashMap<String, CardAccountMock>();

    private PCIMock() {
    }

    CardAccountMock getAccount(PaymentInstrument paymentInstrument) {
        if (paymentInstrument.isInstanceOf(Token.class)) {
            return accounts.get("T" + paymentInstrument.<Token> cast().code().getValue());
        } else {
            CCInformation ccinfo = paymentInstrument.<CCInformation> cast();
            CardAccountMock account = accounts.get("C" + ccinfo.creditCardNumber().getValue());
            if (account == null) {
                account = new CardAccountMock(ccinfo);
                accounts.put("C" + ccinfo.creditCardNumber().getValue(), account);
            }
            return account;
        }
    }

    PaymentResponse createToken(CCInformation ccinfo, Token token) {
        if (accounts.containsKey("T" + token.code().getValue())) {
            return createResponse("1102", "TOKEN ALREADY EXISTS");
        } else {
            CardAccountMock account = getAccount(ccinfo);
            accounts.put("T" + token.code().getValue(), account);
            return createResponse("0000", "TOKEN ADDED");
        }
    }

    PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request) {
        boolean convenienceFee = true;
        if (request.convenienceFee().isNull() && request.convenienceFeeReferenceNumber().isNull()) {
            convenienceFee = false;
        }

        CardAccountMock account = getAccount(request.paymentInstrument());
        if (account == null) {
            return createResponse("1101", "TOKEN NOT FOUND");
        } else {
            if (account.sale(request.amount().getValue())) {

                if (convenienceFee) {
                    account.sale(request.convenienceFee().getValue());
                    ConvenienceFeeMock.instance().addFee(request);
                }

                return createResponse("0000", "OK");
            } else {
                return createResponse("0001", "Credit limit exceeded");
            }

        }
    }

}
