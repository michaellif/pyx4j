/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2011
 * @author kostya
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

enum CaledonTokenAction {

    ADD("ADD", "ADD/CREATE the token"),

    UPDATE("UPDATE", "Used to modify credit card number, expiry and reference data related to the token"),

    DEACTIVATE("DEACTIVATE", "Used to deactivate the token. Tokens which are deactivated will be deleted after 1 year"),

    REACTIVATE("REACTIVATE", "Used to reactivate the token");

    private final String value;

    private final String description;

    private CaledonTokenAction(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
