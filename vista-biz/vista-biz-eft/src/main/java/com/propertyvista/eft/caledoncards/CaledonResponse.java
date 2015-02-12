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
 */
package com.propertyvista.eft.caledoncards;

public class CaledonResponse {

    @HttpResponseField("CODE")
    public String code;

    @HttpResponseField("TEXT")
    public String text;

    @HttpResponseField("AUTH")
    public String authorizationNumber;

    @HttpResponseField("ECHO")
    public String echo;

    @HttpResponseField("DUP")
    public String duplicate;

    @HttpResponseField("CTYPE")
    public String cardType;

    @HttpResponseField("CCO")
    public String countryOfOperationCode;

    @HttpResponseField("CPROD")
    public String cardProduct;

    @HttpResponseField("CVV2")
    public String cvv2;

    @HttpResponseField("EXP")
    public String expiryDate;

    @HttpResponseField("LAST4")
    public String last4Digits;

    @HttpResponseField("RESP_TYPE")
    public String responseType;

}
