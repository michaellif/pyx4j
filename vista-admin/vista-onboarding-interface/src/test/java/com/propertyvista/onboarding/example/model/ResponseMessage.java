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
package com.propertyvista.onboarding.example.model;

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

        SystemDown,

        SystemError

    }

    /**
     * Status of the processing of complete request.
     */
    @XmlElement
    public StatusCode status;

    /**
     * Contains the error message when Vista CRM running in debug mode
     */
    @XmlElement
    public String errorMessage;

    /**
     * Unique identifier for the XML message.
     * Returned unchanged from the request.
     */
    @XmlElement
    public String messageId;

    @XmlElementWrapper
    public List<Response> responses;

    public void addResponse(Response response) {
        if (responses == null) {
            responses = new ArrayList<Response>();
        }
        responses.add(response);
    }
}
