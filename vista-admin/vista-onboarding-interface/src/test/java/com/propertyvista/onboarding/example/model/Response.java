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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

public class Response {

    /**
     * Status of the processing of single request.
     */
    @XmlElement
    @NotNull
    public boolean success;

    /**
     * Contains the error message when Vista CRM running in debug mode
     */
    @XmlElement
    public String errorMessage;

    /**
     * If you submitted a Request with the 'requestId' field, this field will be present in the reply and will contain the exact data you placed in the
     * requestID field of the Request.
     */
    @Size(max = 60)
    @XmlElement
    public String requestId;

}
