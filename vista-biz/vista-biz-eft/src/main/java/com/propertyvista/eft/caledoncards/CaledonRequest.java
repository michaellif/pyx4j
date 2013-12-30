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
package com.propertyvista.eft.caledoncards;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.Size;

public class CaledonRequest {

    @HttpRequestField(value = "TERMID", first = true)
    @Size(max = 8)
    public String terminalID;

    @HttpRequestField("PASS")
    public String password;

    @HttpRequestField("CARD")
    @Size(max = 25)
    public String creditCardNumber;

    @HttpRequestField("EXP")
    public String expiryDate;

    public void setExpiryDate(Date expiry) {
        this.expiryDate = new SimpleDateFormat("MMyy").format(expiry);
    }

    @HttpRequestField("TOKEN")
    public String token;

    @HttpRequestField("DESC")
    public String merchantDescription;

    @HttpRequestField("REF")
    @Size(max = 60)
    public String referenceNumber;

    @HttpRequestField("RESEND")
    public String resend;

    @HttpRequestField("SHOWDUP")
    public String showDuplicate;

    @HttpRequestField("AMT")
    public String amount;

    public void setAmount(BigDecimal value) {
        this.amount = CaledonCardsUtils.formatAmount(value);
    }

    @HttpRequestField("TYPE")
    public String transactionType;

    @HttpRequestField("ECHO")
    public String echo;

    @HttpRequestField("CVV2")
    public String cvv;

    @HttpRequestField("AVS")
    public String avs;

}
