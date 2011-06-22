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

public enum TransactionType {

    SALE("Sale"),

    AUTH_ONLY("Auth Only"),

    AUTH_REVERSE("Auth Reversal"),

    RETURN("Return"),

    RETURN_VOID("Return Void"),

    COMPLETION("Completion");

    private final String description;

    private TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
