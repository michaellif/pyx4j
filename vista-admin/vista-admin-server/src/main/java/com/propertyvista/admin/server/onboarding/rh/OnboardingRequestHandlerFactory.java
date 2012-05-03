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
package com.propertyvista.admin.server.onboarding.rh;

import com.propertyvista.admin.server.onboarding.rhf.RequestHandlerFactory;

public class OnboardingRequestHandlerFactory extends RequestHandlerFactory {

    @Override
    protected void bind() {
        bind(CreateOnboardingUserRequestHandler.class);
        bind(OnboardingUserAuthenticationRequestHandler.class);
        bind(CheckAvailabilityRequestHandler.class);
        bind(GetReCaptchaPublicKeyRequestHandler.class);
        bind(OnboardingUserPasswordChangeRequestHandler.class);

        bind(OnboardingUserSendPasswordResetTokenRequestHandler.class);
        bind(OnboardingUserTokenValidationRequestHandler.class);
        bind(OnboardingUserPasswordResetRequestHandler.class);
        bind(ReservDnsNameRequestHandler.class);
        bind(UpdateBankAccountInfoRequestHandler.class);
    }
}
