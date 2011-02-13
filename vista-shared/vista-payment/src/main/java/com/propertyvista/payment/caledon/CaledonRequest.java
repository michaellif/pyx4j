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
package com.propertyvista.payment.caledon;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class CaledonRequest {

    @HttpRequestField("TERMID")
    String terminalID;

    @HttpRequestField("CARD")
    String creditCardNumber;

    @HttpRequestField("EXP")
    String expiryDate;

    public void setExpiryDate(Date expiry) {
        this.expiryDate = new SimpleDateFormat("MMyy").format(expiry);
    }

    @HttpRequestField("DESC")
    String merchantDescription;

    @HttpRequestField("REF")
    String referenceNumber;

    @HttpRequestField("AMT")
    String amount;

    public void setAmount(float amount) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setGroupingUsed(false);
        this.amount = nf.format(amount * 100);
    }

    @HttpRequestField("TYPE")
    String transactionType;
}
