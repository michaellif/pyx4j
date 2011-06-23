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
    private TransactionType txnType;

    /**
     * This field indicates whether a transaction has been previously sent.
     * 
     * You can set this field in order to receive the response to an already submitted transaction within the past 48 hours.
     * If the system didn't receive the original transaction, it will be processed as a new transaction. The end result is the same, regardless of the cause of
     * the communications problem. You may resubmit the transaction as many times as necessary in order to receive your response.
     */
    private Boolean resend;

    private PaymentInstrument paymentInstrument;

    /**
     * This 3 or 4 character field is used to ensure the physical presence of a card in an environment where the cardholder is not present at the time of the
     * purchase. This value appears as additional characters following the credit card number which is printed within the signature panel on the back of the
     * card.
     */
    private String cvv;

    /**
     * Address Verification Service (AVS) allows cardholder address information to be included with a credit card transaction for comparison with the address
     * that the card issuer has on file.
     */
    private String avs;

    /**
     * The amount of money for which the transaction is being performed.
     * Fractions of cents are ignored.
     */
    private float amount;

    private String reference;

    @XmlElement(required = true)
    public TransactionType getTxnType() {
        return txnType;
    }

    public void setTxnType(TransactionType txnType) {
        this.txnType = txnType;
    }

    @XmlElement(required = false, defaultValue = "false")
    public Boolean getResend() {
        return resend;
    }

    public void setResend(Boolean resend) {
        this.resend = resend;
    }

    //@formatter:off
    @XmlElements({ 
        @XmlElement(name = "creditCard", type = CreditCardInfo.class), 
        @XmlElement(name = "token", type = TokenPaymentInstrument.class)})
    //@formatter:on    
    public PaymentInstrument getPaymentInstrument() {
        return paymentInstrument;
    }

    public void setPaymentInstrument(PaymentInstrument paymentInstrument) {
        this.paymentInstrument = paymentInstrument;
    }

    @XmlElement(required = false)
    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @XmlElement(required = false)
    public String getAvs() {
        return avs;
    }

    public void setAvs(String avs) {
        this.avs = avs;
    }

    @XmlElement(required = true)
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @XmlElement(required = true)
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
