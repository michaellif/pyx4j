/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding.example.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.onboarding.OnboardingUserAuthenticationResponseIO;

/**
 * 
 * @see OnboardingUserAuthenticationResponseIO
 * 
 */
public class OnboardingUserAuthenticationResponse extends Response {

    public enum AuthenticationStatusCode {

        OK,

        OK_PasswordChangeRequired,

        PermissionDenied,

        AuthenticationFailed,

        PasswordChangeRequired,

        ChallengeVerificationRequired;

    }

    /**
     * Status of the processing of complete request.
     */
    @XmlElement(required = true)
    @NotNull
    public AuthenticationStatusCode status;

    @XmlElement
    public String onboardingAccountId;

    /**
     * Returned when status == ChallengeVerificationRequired
     */
    public String reCaptchaPublicKey;
}
