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
    @XmlElement(required = false)
    public String requestID;

    /**
     * This is the 4-digit response code from the transaction. "0000" constitutes a successful transaction, and any other 4-digit code constitutes a failure.
     */
    public String code;

    public String text;

    public String auth;

    /**
     * For transactions where AVS is used (Sale, Pre-Authorization, Auth Only) and AVS data has been provided, this field returns the AVS result code. This code
     * is a 1-letter response indicating the closeness of the address match.
     */
    public String avsResultCode;

    /**
     * If you submitted a Request with the 'ECHO' field, this field will be present in the reply and will contain the exact data you placed in the ECHO
     * field of the Request.
     */
    public String echo;
}
