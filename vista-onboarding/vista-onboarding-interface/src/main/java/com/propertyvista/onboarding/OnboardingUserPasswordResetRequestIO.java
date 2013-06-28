/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * Login using user using token in DB
 * 
 * @see OnboardingUserAuthenticationResponseIO
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface OnboardingUserPasswordResetRequestIO extends RequestIO {

    @NotNull
    IPrimitive<String> token();

    IPrimitive<String> securityAnswer();

    @NotNull
    @LogTransient
    IPrimitive<String> newPassword();

    /**
     * Do not send Id it will be returned in OnboardingUserAuthenticationResponseIO
     */
    @Override
    @XmlTransient
    IPrimitive<String> onboardingAccountId();

}
