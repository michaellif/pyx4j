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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.propertyvista.onboarding.OnboardingUserAuthenticationRequestIO;

/**
 * @see OnboardingUserAuthenticationRequestIO
 */
public class OnboardingUserAuthenticationRequest extends Request {

    @NotNull
    @XmlElement
    public String email;

    @NotNull
    @XmlElement
    public String password;

    @XmlTransient
    public String onboardingAccountId;
}
