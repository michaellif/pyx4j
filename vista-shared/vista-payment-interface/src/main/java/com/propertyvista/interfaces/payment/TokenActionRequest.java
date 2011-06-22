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

        ADD("ADD/CREATE the token"),

        UPDATE("Used to modify credit card number, expiry and reference data related to the token"),

        DEACTIVATE("Used to deactivate the token. Tokens which are deactivated will be deleted after 1 year"),

        REACTIVATE("Used to reactivate the token");

        private final String description;

        private TokenAction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @XmlElement(required = true)
    public TokenAction action;

    @XmlElement(required = true)
    public String code;

    @XmlElement(required = false)
    public String reference;

    @XmlElement(required = false)
    public CreditCardInfo card;

}
