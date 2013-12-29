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
import java.math.RoundingMode;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.payment.FeeCalulationRequest;
import com.propertyvista.payment.FeeCalulationResponse;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentRequest;

class ConvenienceFeeMock {

    private static class SingletonHolder {
        public static final ConvenienceFeeMock INSTANCE = new ConvenienceFeeMock();
    }

    static ConvenienceFeeMock instance() {
        return SingletonHolder.INSTANCE;
    }

    private BigDecimal balance = BigDecimal.ZERO;

    private ConvenienceFeeMock() {
    }

    BigDecimal getBalance() {
        return balance;
    }

    FeeCalulationResponse getConvenienceFee(Merchant merchant, FeeCalulationRequest request) {
        FeeCalulationResponse response = EntityFactory.create(FeeCalulationResponse.class);
        response.success().setValue(true);
        response.code().setValue("0000");

        BigDecimal feePercent = new BigDecimal(".10");
        response.feeAmount().setValue(request.amount().getValue().multiply(feePercent).setScale(2, RoundingMode.HALF_UP));
        response.totalAmount().setValue(request.amount().getValue().add(response.feeAmount().getValue()));
        return response;
    }

    void addFee(PaymentRequest request) {
        // TODO some validations

        balance = balance.add(request.convenienceFee().getValue());

    }

    void voidFee(PaymentRequest request) {
        // TODO some validations

        balance = balance.subtract(request.convenienceFee().getValue());
    }
}
