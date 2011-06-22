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

enum CaledonTransactionType {

    //external api
    SALE("S", "Sale"),

    //external api
    AUTH_ONLY("A", "Auth Only"),

    VOID("V", "Void"),

    //external api
    AUTH_REVERSE("A", "Auth Reversal"),

    PREAUTH("P", "Pre-Authorization"),

    //external api
    RETURN("R", "Return"),

    //external api
    RETURN_VOID("M", "Return Void"),

    FORCE_POST("F", "Force Post"),

    //external api
    COMPLETION("C", "Completion"),

    BALANCE_REQ("B", "Balance Request"),

    SETTLEMENT("D", "Settlement"),

    DISCARD_BATCH("T", "Discard (Toast) batch"),

    CONTRA_ADD("+", "Contra add"),

    CONTRA_DELETE("-", "Contra delete"),

    CONTRA_QUERY("Q", "Contra query"),

    LEVEL_3_DETAIL("L", "Level 3 detail delivery"),

    //external api
    CANADIAN_AVS("I", "Canadian Address Verification Service(AVS)"),

    COMMERCIAL("K", "Commercial"),

    //external api
    TOKEN("G", "Token");

    private final String value;

    private final String description;

    private CaledonTransactionType(String value, String description) {
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
