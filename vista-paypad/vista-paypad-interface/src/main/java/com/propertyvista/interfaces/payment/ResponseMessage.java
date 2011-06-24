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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResponseMessage {

    public enum StatusCode {

        OK,

        MessageFormatError,

        AuthenticationFailed,

        ReadOnly,

        SystemDown

    }

    /**
     * Unique identifier for the XML message.
     * Returned unchanged from the request.
     */
    private String messageID;

    /**
     * Returned unchanged from the request.
     */
    private String merchantId;

    /**
     * Status of the processing of complete request.
     */
    private StatusCode status;

    private List<Response> responses;

    @XmlElement(required = false)
    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    @XmlElement(required = false)
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @XmlElement(required = true)
    public StatusCode getStatus() {
        return status;
    }

    public void setStatus(StatusCode status) {
        this.status = status;
    }

    @XmlElementWrapper
    public List<Response> getResponse() {
        return responses;
    }

    public void setResponse(List<Response> response) {
        this.responses = response;
    }

    public void addResponse(Response response) {
        if (responses == null) {
            responses = new ArrayList<Response>();
        }
        responses.add(response);
    }
}
