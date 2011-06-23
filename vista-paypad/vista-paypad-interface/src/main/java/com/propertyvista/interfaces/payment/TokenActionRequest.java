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

public class TokenActionRequest extends Request {

    public enum TokenAction {

        /**
         * Used to add/create the token
         */
        Add,

        /**
         * Used to modify credit card number, expiry and reference data related to the token
         */
        Update,

        /**
         * Used to deactivate the token. Tokens which are deactivated will be deleted after 1 year
         */
        Deactivate,

        /**
         * Used to reactivate the token
         */
        Reactivate;

    }

    /**
     * This field is used to determine what action is to be performed for the token.
     */
    private TokenAction action;

    private String code;

    /**
     * A reference data field that can be associated with the token.
     */
    private String reference;

    private CreditCardInfo card;

    @XmlElement(required = true)
    public TokenAction getAction() {
        return action;
    }

    public void setAction(TokenAction action) {
        this.action = action;
    }

    @XmlElement(required = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElement(required = false)
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @XmlElement(required = false)
    public CreditCardInfo getCard() {
        return card;
    }

    public void setCard(CreditCardInfo card) {
        this.card = card;
    }

}
