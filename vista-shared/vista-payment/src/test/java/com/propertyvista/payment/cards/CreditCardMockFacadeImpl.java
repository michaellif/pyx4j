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

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.payment.PaymentInstrument;
import com.propertyvista.payment.Token;

public class CreditCardMockFacadeImpl implements CreditCardMockFacade {

    @Override
    public BigDecimal getConvenienceFeeBalance() {
        return ConvenienceFeeMock.instance().getBalance();
    }

    private static PaymentInstrument createPaymentInstrument(CreditCardInfo cc) {
        Token token = EntityFactory.create(Token.class);
        token.code().setValue(cc.token().getStringView());
        token.cardType().setValue(cc.cardType().getValue());
        return token;
    }

    @Override
    public BigDecimal getAccountBalance(AbstractPaymentMethod paymentMethod) {
        CardAccountMock account = PCIMock.instance().getAccount(createPaymentInstrument(paymentMethod.details().<CreditCardInfo> cast()));
        return account.balance;
    }

}
