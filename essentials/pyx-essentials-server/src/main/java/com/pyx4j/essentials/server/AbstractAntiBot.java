/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 16, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;

/**
 * Empty implementation of AntiBot, Application should redefine its own implementation
 */
public abstract class AbstractAntiBot {

    public static final String GENERIC_LOGIN_FAILED_MESSAGE = "Invalid login/password";

    private static final I18n i18n = I18n.get(AbstractAntiBot.class);

    public static enum LoginType {

        userLogin,

        accessToken
    }

    public abstract void assertCaptcha(String challenge, String response);

    protected abstract boolean isCaptchaRequired(LoginType loginType, String email);

    protected abstract boolean onAuthenticationFailed(LoginType loginType, String email);

    /**
     * Verify if there are may login attempts with this email
     * 
     * @param email
     * @param challengeResponse
     *            is optional
     */
    public static void assertLogin(LoginType loginType, String email, Pair<String, String> challengeResponse) {
        AbstractAntiBot ab = ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getAntiBot();
        if (ab == null) {
            throw new UserRuntimeException(GENERIC_LOGIN_FAILED_MESSAGE);
        }
        if (ab.isCaptchaRequired(loginType, email)) {
            if ((challengeResponse == null) || CommonsStringUtils.isEmpty(challengeResponse.getA()) || CommonsStringUtils.isEmpty(challengeResponse.getB())) {
                throw new ChallengeVerificationRequired(i18n.tr("Human Verification Is Required"));
            }
            ab.assertCaptcha(challengeResponse.getA(), challengeResponse.getB());
        }
    }

    public static void assertCaptcha(Pair<String, String> challengeRresponse) {
        if (challengeRresponse == null) {
            throw new UserRuntimeException(GENERIC_LOGIN_FAILED_MESSAGE);
        }
        ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getAntiBot().assertCaptcha(challengeRresponse.getA(),
                challengeRresponse.getB());
    }

    /**
     * @param email
     * @return true is there was too many failed log-in attempts for given E-mail
     */
    public static boolean authenticationFailed(LoginType loginType, String email) {
        AbstractAntiBot ab = ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getAntiBot();
        if (ab != null) {
            return ab.onAuthenticationFailed(loginType, email);
        } else {
            return false;
        }
    }
}
