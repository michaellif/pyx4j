/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.AbstractUserCredential;

public class AccessKey {

    private static final I18n i18n = I18n.get(AccessKey.class);

    public static final int ACCESS_KEY_LEN = 16;

    public static class TokenParser {

        public String email;

        public String accessKey;

        public TokenParser(String base64Token) {
            if (base64Token == null) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
            }

            String token = new String(Base64.decodeBase64(base64Token), Charset.forName("ISO-8859-1"));

            if (CommonsStringUtils.isEmpty(token)) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
            }
            int idx = token.indexOf('|');
            if (idx <= 4) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
            }
            email = token.substring(0, idx);
            accessKey = token.substring(idx + 1);
            if (accessKey.length() != ACCESS_KEY_LEN) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
            }
            validateAccessKey(accessKey);
        }
    }

    public static String createAccessKey() {
        StringBuffer buf = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < ACCESS_KEY_LEN; i++) {
            char c = (char) (rnd.nextInt('z' - 'a') + 'a');
            buf.append(c);
        }
        return buf.toString();
    }

    public static void validateAccessKey(String accessKey) {
        if ((accessKey == null) || (accessKey.length() != ACCESS_KEY_LEN)) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
        }
        if (!accessKey.matches("[a-z]{" + ACCESS_KEY_LEN + "}?")) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid Request"));
        }
    }

    public static String compressToken(String email, String accessKey) {
        return Base64.encodeBase64URLSafeString((email + "|" + accessKey).getBytes(Charset.forName("ISO-8859-1")));
    }

    public static <U extends AbstractUser, E extends AbstractUserCredential<U>> String createAccessToken(U user, Class<E> credentialClass, int keyExpireDays) {
        E credential = Persistence.service().retrieve(credentialClass, user.getPrimaryKey());
        if (credential == null) {
            return null;
        }
        if (!credential.enabled().isBooleanTrue()) {
            return null;
        }

        credential.accessKey().setValue(AccessKey.createAccessKey());
        Calendar expire = new GregorianCalendar();
        expire.add(Calendar.DATE, keyExpireDays);
        credential.accessKeyExpire().setValue(expire.getTime());
        Persistence.service().persist(credential);
        Persistence.service().commit();
        if (user.isValueDetached()) {
            Persistence.service().retrieve(user);
        }
        return compressToken(user.email().getValue(), credential.accessKey().getValue());
    }
}
