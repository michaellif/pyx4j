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
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlType(name = "Payment")
@XmlRootElement(name = "payment")
public class PaymentIO extends TransactionIO {

    //   List<ChargeRS> charges;

    public StringIO paymentType;

    public PaymentIO() {
        super();
    }

    public PaymentIO(BigDecimalIO amount) {
        this(new StringIO("Payment"), amount);
    }

    public PaymentIO(StringIO description, BigDecimalIO amount) {
        super(description, amount);
    }

}
