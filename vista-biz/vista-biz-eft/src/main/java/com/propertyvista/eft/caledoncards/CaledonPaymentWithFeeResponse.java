/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoncards;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CaledonPaymentWithFeeResponse extends CaledonFeeResponseBase {

    @HttpResponseField(value = "fee_reference_number")
    @NotNull
    @Size(max = 30)
    public String referenceNumberFeeCalulation;

    @HttpResponseField(value = "reference_number")
    @NotNull
    @Size(max = 60)
    public String referenceNumber;

    @HttpResponseField(value = "card_product")
    public String cardProduct;

    @HttpResponseField("card_number")
    public String creditCardNumber;

    @HttpResponseField("expiry_date")
    public String expiryDate;

    @HttpResponseField("token")
    @Size(max = 30)
    public String token;

    @HttpResponseField("amount")
    @NotNull
    @Size(max = 10)
    public String amount;

    public BigDecimal getAmount() {
        return CaledonCardsUtils.parsAmount(amount);
    }

    @HttpResponseField("fee_amount")
    @NotNull
    @Size(max = 8)
    public String feeAmount;

    public BigDecimal getFeeAmount() {
        return CaledonCardsUtils.parsAmount(feeAmount);
    }

    @HttpResponseField("total_amount")
    @NotNull
    @Size(max = 10)
    public String totalAmount;

    public BigDecimal getTotalAmount() {
        return CaledonCardsUtils.parsAmount(totalAmount);
    }

    @HttpResponseField("fee_response")
    public String responseFeeAuthorization;

    @HttpResponseField("payment_response")
    public String responsePaymentAuthorization;

}
