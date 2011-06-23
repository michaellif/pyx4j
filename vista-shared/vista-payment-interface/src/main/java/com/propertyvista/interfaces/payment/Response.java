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

public class Response {

    /**
     * If you submitted a Request with the 'requestID' field, this field will be present in the reply and will contain the exact data you placed in the
     * requestID field of the Request.
     */
    private String requestID;

    /**
     * This is the 4-digit response code from the transaction. "0000" constitutes a successful transaction, and any other 4-digit code constitutes a failure.
     */
    private String code;

    /**
     * This is the response text of the message. Historically this is what would appear on the screen or printout of a credit card terminal. The authorization
     * number and amount are displayed here on an approval-type transaction. For errors and decline responses this field contains the error message or decline
     * message.
     */
    private String text;

    /**
     * For transactions which return authorization numbers (Sale, PreAuthorization, Completion, AuthOnly) this is the authorization number. The
     * authorization number is also returned in the TEXT field,
     */
    private String auth;

    /**
     * For transactions where AVS is used (Sale, Pre-Authorization, Auth Only) and AVS data has been provided, this field returns the AVS result code. This code
     * is a 1-letter response indicating the closeness of the address match.
     */
    private String avsResultCode;

    /**
     * If you submitted a Request with the 'ECHO' field, this field will be present in the reply and will contain the exact data you placed in the ECHO
     * field of the Request.
     */
    private String echo;

    @XmlElement(required = false)
    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    @XmlElement(required = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElement(required = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAvsResultCode() {
        return avsResultCode;
    }

    public void setAvsResultCode(String avsResultCode) {
        this.avsResultCode = avsResultCode;
    }

    public String getEcho() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo;
    }
}
