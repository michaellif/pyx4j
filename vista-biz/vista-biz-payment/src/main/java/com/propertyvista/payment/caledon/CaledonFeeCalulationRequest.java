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
package com.propertyvista.payment.caledon;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CaledonFeeCalulationRequest {

    @HttpRequestField(value = "type", first = true)
    @NotNull
    public final String type = CaledonFeeRequestTypes.FeeCalulation.getIntrfaceValue();

    @HttpRequestField(value = "terminal_id")
    @NotNull
    @Size(max = 8)
    public String terminalID;

    @HttpRequestField(value = "fee_reference_number")
    @NotNull
    @Size(max = 30)
    public String referenceNumber;

    @HttpRequestField(value = "card_product")
    public String cardProduct;

    public void setCardProduct(CaledonCardProduct product) {
        this.cardProduct = product.getIntrfaceValue();
    }

    @HttpRequestField("card_number")
    public String creditCardNumber;

    @HttpRequestField("expiry_date")
    public String expiryDate;

    public void setExpiryDate(Date expiry) {
        this.expiryDate = new SimpleDateFormat("MMyy").format(expiry);
    }

    @HttpRequestField("token")
    @Size(max = 30)
    public String token;

    @HttpRequestField("amount")
    @NotNull
    @Size(max = 10)
    public String amount;

    public void setAmount(BigDecimal value) {
        this.amount = CaledonCardsUtils.formatAmount(value);
    }

}
