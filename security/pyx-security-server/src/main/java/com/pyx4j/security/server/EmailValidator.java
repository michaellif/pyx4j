/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-12-30
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.server;

import java.util.Locale;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.pyx4j.commons.ValidationUtils;

public class EmailValidator {

    public static String normalizeEmailAddress(String email) {
        return email.toLowerCase(Locale.ENGLISH).trim();
    }

    public static boolean isValid(String email) {
        if (!ValidationUtils.isValidEmail(email)) {
            return false;
        }

        try {
            new InternetAddress(email);
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

}
