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
package com.propertyvista.payment;

public interface IPaymentProcessor {

    PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request);

    PaymentResponse realTimeAuthorization(Merchant merchant, PaymentRequest request);

    PaymentResponse createToken(Merchant merchant, CCInformation ccinfo, Token token);

    PaymentResponse updateToken(Merchant merchant, CCInformation ccinfo, Token token);

    PaymentResponse deactivateToken(Merchant merchant, Token token);

    PaymentResponse reactivateToken(Merchant merchant, Token token);

    PaymentResponse tokenSale(Merchant merchant, PaymentRequest request);

}
