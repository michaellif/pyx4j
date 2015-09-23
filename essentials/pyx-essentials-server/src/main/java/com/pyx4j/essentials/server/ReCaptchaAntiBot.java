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
 */
package com.pyx4j.essentials.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaException;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class ReCaptchaAntiBot extends LoginAttemptsCountAntiBot {

    private final static Logger log = LoggerFactory.getLogger(ReCaptchaAntiBot.class);

    private static final I18n i18n = I18n.get(ReCaptchaAntiBot.class);

    @Override
    public void assertCaptcha(String challenge, String response) {
        if (CommonsStringUtils.isEmpty(challenge) || CommonsStringUtils.isEmpty(response)) {
            throw new UserRuntimeException(i18n.tr("Are you a robot?"));
        }
        if (challenge.equals("reCAPTCHA-v2")) {
            ReCaptchaV2APIClient.instance().assertCaptcha(response);
        } else {
            assertCaptchaV1(challenge, response);
        }
        log.debug("CAPTCHA Ok");
    }

    public void assertCaptchaV1(String challenge, String response) {
        String privateKey = ServerSideConfiguration.instance(EssentialsServerSideConfiguration.class).getReCaptchaPrivateKey();
        String publicKey = ServerSideConfiguration.instance(EssentialsServerSideConfiguration.class).getReCaptchaPublicKey();
        ReCaptcha rc = ReCaptchaFactory.newReCaptcha(publicKey, privateKey, false);
        ReCaptchaResponse captchaResponse;
        try {
            captchaResponse = rc.checkAnswer(getRequestRemoteAddr(), challenge, response);
        } catch (ReCaptchaException e) {
            log.error("Error", e);
            throw new RuntimeExceptionSerializable(i18n.tr("reCAPTCHA Connection Failed"));
        }
        if (!captchaResponse.isValid()) {
            if ("incorrect-captcha-sol".equals(captchaResponse.getErrorMessage())) {
                throw new UserRuntimeException(i18n.tr("The CAPTCHA Solution You Entered Was Incorrect"));
            } else {
                throw new RuntimeExceptionSerializable(captchaResponse.getErrorMessage());
            }
        }
    }

}
