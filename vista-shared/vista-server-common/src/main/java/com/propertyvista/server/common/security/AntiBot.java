/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;

public class AntiBot {

    private final static Logger log = LoggerFactory.getLogger(AntiBot.class);

    private static final long LIFE_DURATION = 3 * Consts.MIN2MSEC;

    public static void assertCaptcha(Pair<String, String> challengeRresponse) {
        if (ServerSideConfiguration.instance().isDevelopmentBehavior() && challengeRresponse.getB().equals("x")) {
            log.debug("Development CAPTCHA Ok");
            return;
        }
        ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getAntiBot().assertCaptcha(challengeRresponse.getA(),
                challengeRresponse.getB());
    }

    public static class InvalidLoginAttempts {

        List<Long> when = new Vector<Long>();

        boolean isCaptchaRequired() {
            long now = System.currentTimeMillis();
            Iterator<Long> it = when.iterator();
            int active = 0;
            while (it.hasNext()) {
                if (it.next() < now - LIFE_DURATION) {
                    it.remove();
                } else {
                    active++;
                }
            }
            return (active >= 3);
        }
    }

    private static Map<String, InvalidLoginAttempts> cache = new HashMap<String, InvalidLoginAttempts>();

    public static boolean authenticationFailed(String email) {
        InvalidLoginAttempts la = cache.get(email);
        if (la == null) {
            la = new InvalidLoginAttempts();
            cache.put(email, la);
        }
        la.when.add(System.currentTimeMillis());
        return la.isCaptchaRequired();
    }

    public static boolean isCaptchaRequired(String email) {
        InvalidLoginAttempts la = cache.get(email);
        if (la == null) {
            return false;
        } else {
            return la.isCaptchaRequired();
        }
    }
}
