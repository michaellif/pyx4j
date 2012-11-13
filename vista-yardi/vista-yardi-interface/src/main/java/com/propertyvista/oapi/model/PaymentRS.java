/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.model;

import java.math.BigDecimal;

public class PaymentRS extends TransactionRS {

    public PaymentRS() {
        super();
    }

    public PaymentRS(BigDecimal amount) {
        this("Payment", amount);
    }

    public PaymentRS(String description, BigDecimal amount) {
        super(description, amount);
    }

}
