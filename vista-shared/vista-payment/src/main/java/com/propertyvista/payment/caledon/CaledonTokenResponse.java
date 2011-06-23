/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author kostya
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

public enum CaledonTokenResponse {

    TOKEN_SUCCESS("0000", "Token operation SUCCESSFULLY executed"),

    TOKEN_NOT_FOUND("1101", "TOKEN_NOT_FOUND"),

    TOKEN_ALREADY_EXISTS("1102", "TOKEN_ALREADY_EXISTS"),

    TOKEN_COMMUNICATIONS_ERROR("1103", "TOKEN_COMMUNICATIONS_ERROR"),

    TOKEN_BAD_DATA_FORMAT("1104", "TOKEN_BAD_DATA_FORMAT"),

    TOKEN_INVALID_TOKEN_ACTION("1105", "TOKEN_INVALID_TOKEN_ACTION"),

    TOKEN_ACCESS_DENIED("1001", "TOKEN_ACCESS_DENIED"),

    TRAN_TYPE_INVALID("1019", "TRAN_TYPE_INVALID");

    private final String value;

    private final String description;

    private CaledonTokenResponse(String value, String description) {
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
