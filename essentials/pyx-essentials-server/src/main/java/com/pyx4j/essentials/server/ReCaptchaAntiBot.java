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

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaException;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

import com.pyx4j.server.contexts.Context;

public class ReCaptchaAntiBot extends LoginAttemptsCountAntiBot {

    /**
     * TODO Use build server configuration file to load the keys from resources.
     * 
     * @param serverName
     *            the host name of the server to which the request was sent.
     * @return
     */
    protected String reCaptchaPrivateKey(String serverName) {
        return "6LdBxgoAAAAAALYkW6J6JDYH-Q10M-sfvvCGhs9y";
    }

    protected String reCaptchaPublicKey(String serverName) {
        return "6LdBxgoAAAAAAP7RdZ3kbHwVA99j1qKB97pdo6Mq";
    }

    @Override
    protected void assertCaptcha(String email, String challenge, String response) {
        String serverName = Context.getRequestServerName();
        ReCaptcha rc = ReCaptchaFactory.newReCaptcha(reCaptchaPublicKey(serverName), reCaptchaPrivateKey(serverName), false);
        ReCaptchaResponse captchaResponse;
        try {
            captchaResponse = rc.checkAnswer(Context.getRequestRemoteAddr(), challenge, response);
        } catch (ReCaptchaException e) {
            throw new RuntimeException("reCAPTCHA connection failed");
        }

        if (!captchaResponse.isValid()) {
            throw new RuntimeException(captchaResponse.getErrorMessage());
        }
    }

}
