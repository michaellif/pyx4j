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
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.i18n.shared.I18n;

public class AccessKey {

    private static I18n i18n = I18n.get(AccessKey.class);

    public static final int ACCESS_KEY_LEN = 16;

    public static class TokenParser {

        public String email;

        public String accessKey;

        public TokenParser(String base64Token) {
            if (base64Token == null) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid request"));
            }

            String token = new String(Base64.decodeBase64(base64Token), Charset.forName("ISO-8859-1"));

            if (CommonsStringUtils.isEmpty(token)) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid request"));
            }
            int idx = token.indexOf('|');
            if (idx <= 4) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid request"));
            }
            email = token.substring(0, idx);
            accessKey = token.substring(idx + 1);
            if (accessKey.length() != ACCESS_KEY_LEN) {
                throw new RuntimeExceptionSerializable(i18n.tr("Invalid request"));
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
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid request"));
        }
        if (!accessKey.matches("[a-z]{" + ACCESS_KEY_LEN + "}?")) {
            throw new RuntimeExceptionSerializable(i18n.tr("Invalid request"));
        }
    }

    public static String compressToken(String email, String accessKey) {
        return Base64.encodeBase64URLSafeString((email + "|" + accessKey).getBytes(Charset.forName("ISO-8859-1")));
    }
}
