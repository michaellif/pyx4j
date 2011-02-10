/*
 * My Easy Force
 * Copyright (C) 2009-2010 myeasyforce.com.
 *
 * Created on Jan 29, 2010
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.access;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaException;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.propertyvista.portal.server.VistaServerSideConfiguration;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Pair;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.server.contexts.Context;

public class AntiBot {

    private final static Logger log = LoggerFactory.getLogger(AntiBot.class);

    private static I18n i18n = I18nFactory.getI18n();

    private static final long LIFE_DURATION = 3 * Consts.MIN2MSEC;

    public static void assertCaptcha(Pair<String, String> challengeRresponse) {
        if (challengeRresponse == null || CommonsStringUtils.isEmpty(challengeRresponse.getA()) || CommonsStringUtils.isEmpty(challengeRresponse.getB())) {
            throw new RuntimeExceptionSerializable(i18n.tr("Captcha code is required"));
        }

        String privateKey = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPrivateKey();
        String publicKey = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey();
        ReCaptcha rc = ReCaptchaFactory.newReCaptcha(publicKey, privateKey, false);

        if (ServerSideConfiguration.instance().isDevelopmentBehavior() && challengeRresponse.getB().equals("x")) {
            log.warn("Development CAPTCHA Ok");
            return;
        }
        ReCaptchaResponse captchaResponse;
        try {
            captchaResponse = rc.checkAnswer(Context.getRequestRemoteAddr(), challengeRresponse.getA(), challengeRresponse.getB());
        } catch (ReCaptchaException e) {
            log.error("Error", e);
            throw new RuntimeExceptionSerializable(i18n.tr("reCAPTCHA connection failed"));
        }

        if (!captchaResponse.isValid()) {
            if ("incorrect-captcha-sol".equals(captchaResponse.getErrorMessage())) {
                throw new RuntimeExceptionSerializable(i18n.tr("The CAPTCHA solution was incorrect"));
            } else {
                throw new RuntimeExceptionSerializable(captchaResponse.getErrorMessage());
            }
        }
        log.debug("CAPTCHA Ok");
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
