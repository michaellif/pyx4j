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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class TransactionRequest extends Request {

    public enum TransactionType {

        /**
         * Authorize and request draft capture if successful. If the transaction is successful, at settlement this transaction will be marked for deposit into
         * the merchant's account. This is the transaction type you will use most of the time in order to charge customers.
         */
        Sale,

        /**
         * Authorize, but do not request draft capture. If this transaction is successful, the card's "open to buy" will be reduced, but the transaction will
         * not be marked for deposit at settlement. If you need to charge a customer, you may want to use the Sale transaction type instead.
         */
        AuthorizeOnly,

        /**
         * Return money to the customer's card. The card is not affected at all until deposit, at which time the card will be credited for the returned amount.
         */
        Return,

        /**
         * Authorize, but do not request draft capture. If this transaction is successful, the card's "open to buy" will be reduced, but the transaction will
         * not be marked for draft capture. If you use this transaction type, you can use a Completion transaction in order to cause draft capture. This
         * transaction is suitable for companies where it is not permitted to bill the customer until goods are shipped, and is also useful in restaurants where
         * the tip amount is not yet known.
         */
        PreAuthorization,

        /**
         * PreAuthorization reversal allows the merchant to adjust the authorization amount after the transaction has taken place. The merchant can also process
         * a full reversal, where the transaction is canceled by entering 0 as the replacement amount.
         */
        AuthReversal,

        /**
         * Submit a previously pre-authorized transaction for draft capture. There must be a corresponding Pre-Authorization transaction in the past 30 days in
         * order to use this transaction type.
         * 
         * If you do not want to store card numbers, make sure your reference number is unique within the past 30 days. You can then send "0" (zero) as the card
         * number in order to do a Completion without providing the card number.
         */
        Completion;
    }

    /**
     * This is the kind of transaction you are performing.
     */
    @XmlElement(required = true)
    public TransactionType txnType;

    /**
     * This field indicates whether a transaction has been previously sent.
     * 
     * You can set this field in order to receive the response to an already submitted transaction within the past 48 hours.
     * If the system didn't receive the original transaction, it will be processed as a new transaction. The end result is the same, regardless of the cause of
     * the communications problem. You may resubmit the transaction as many times as necessary in order to receive your response.
     */
    @XmlElement(required = false, defaultValue = "false")
    public Boolean resend;

    //@formatter:off
    @XmlElements({ 
        @XmlElement(name = "CreditCard", type = CreditCardInfo.class), 
        @XmlElement(name = "Token", type = TokenPaymentInstrument.class)})
    public PaymentInstrument paymentInstrument;
    //@formatter:on

    /**
     * The amount of money for which the transaction is being performed.
     * Fractions of cents are ignored.
     */
    @XmlElement(required = true)
    public float amount;

    @XmlElement(required = true)
    public String reference;
}
