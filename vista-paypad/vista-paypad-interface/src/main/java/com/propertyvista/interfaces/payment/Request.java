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

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ TransactionRequest.class, TokenActionRequest.class })
public abstract class Request {

    @Size(max = 60)
    private String requestID;

    /**
     * Data that is in this field is returned in the reply. This can be useful for transaction tracking in single-process or batch applications.
     * This field is returned in the 'ECHO' field of the transaction Response.
     */
    @Size(max = 60)
    @Pattern(regexp = "[A-Za-z0-9-/]+")
    private String echo;

    @XmlElement(required = false)
    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getEcho() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo;
    }
}
