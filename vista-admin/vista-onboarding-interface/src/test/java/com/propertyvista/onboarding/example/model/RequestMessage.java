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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestMessage {

    @Size(max = 60)
    @NotNull
    @XmlElement(required = true)
    public String interfaceEntity;

    /**
     * You must provide your password for every HTTP request.
     */
    @Size(max = 60)
    @NotNull
    @XmlElement(required = true)
    public String interfaceEntityPassword;

    /**
     * Optional unique identifier for the XML message
     */
    @Size(max = 60)
    @XmlElement(required = false)
    public String messageId;

    //@formatter:off
    @XmlElementWrapper
    @XmlElements({ 
        @XmlElement(name = "onboardingUserAuthenticationRequest", type = OnboardingUserAuthenticationRequest.class),
        @XmlElement(name = "createOnboardingUserRequest", type = CreateOnboardingUserRequest.class),
        @XmlElement(name = "checkAvailabilityRequest", type = CheckAvailabilityRequest.class), 
        @XmlElement(name = "createPMCRequest", type = CreatePMCRequest.class),
        @XmlElement(name = "onboardingUserSendPasswordResetTokenRequest", type = OnboardingUserSendPasswordResetTokenRequest.class),
        @XmlElement(name = "reserveDnsNameRequest", type = ReserveDnsNameRequest.class)})
    //@formatter:on    
    public List<Request> requests;

    public void addRequest(Request request) {
        if (requests == null) {
            requests = new ArrayList<Request>();
        }
        this.requests.add(request);
    }

}
