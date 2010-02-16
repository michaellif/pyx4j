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

/**
 * Empty implementation of AntiBot, Application should redefine its own implementation
 */
public abstract class AbstractAntiBot {

    public static final String GENERIC_LOGIN_FAILED_MESSAGE = "Invalid login/password";

    protected abstract void assertCaptcha(String email, String challenge, String response);

    protected abstract boolean isCaptchaRequired(String email);

    protected abstract boolean onAuthenticationFailed(String email);

    public static void assertLogin(String email, Pair<String, String> challengeResponse) {
        AbstractAntiBot ab = ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getAntiBot();
        if (ab == null) {
            throw new RuntimeException(GENERIC_LOGIN_FAILED_MESSAGE);
        }
        if (ab.isCaptchaRequired(email)) {
            if (CommonsStringUtils.isEmpty(challengeResponse.getA()) || CommonsStringUtils.isEmpty(challengeResponse.getB())) {
                throw new RuntimeException(GENERIC_LOGIN_FAILED_MESSAGE);
            }
            ab.assertCaptcha(email, challengeResponse.getA(), challengeResponse.getB());
        }
    }

    /**
     * @param email
     * @return true is there was too many failed log-in attempts for given E-mail
     */
    public static boolean authenticationFailed(String email) {
        AbstractAntiBot ab = ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getAntiBot();
        if (ab != null) {
            return ab.onAuthenticationFailed(email);
        } else {
            return false;
        }
    }
}
