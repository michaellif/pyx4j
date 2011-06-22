/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

public class CreditCardInfo extends PaymentInstrument {

    /**
     * The number on the card, no spaces, dashes, hyphens or any other punctuation are allowed.
     */
    @XmlElement(required = true)
    public String cardNumber;

    /**
     * The expiry date of the card.
     * 
     * Day value in java.util.Date are ignored.
     */
    @XmlSchemaType(name = "date")
    @XmlElement(required = true)
    public Date expiryDate;

    /**
     * This 3 or 4 character field is used to ensure the physical presence of a card in an environment where the cardholder is not present at the time of the
     * purchase. This value appears as additional characters following the credit card number which is printed within the signature panel on the back of the
     * card.
     */
    @XmlElement(required = false)
    public String cvv;
}
