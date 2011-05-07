/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.ReCaptchaAntiBot;

public class VistaAntiBot extends ReCaptchaAntiBot {

    private final static Logger log = LoggerFactory.getLogger(VistaAntiBot.class);

    @Override
    public void assertCaptcha(String challenge, String response) {
        if (ServerSideConfiguration.instance().isDevelopmentBehavior() && "x".equals(response)) {
            log.debug("Development CAPTCHA Ok");
        } else {
            super.assertCaptcha(challenge, response);
        }
    }
}
