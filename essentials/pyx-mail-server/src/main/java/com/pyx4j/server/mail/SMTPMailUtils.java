/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

class SMTPMailUtils {

    static InternetAddress email(String email) throws AddressException {
        if (email == null) {
            return null;
        }
        return new InternetAddress(email.trim());
    }

    static List<InternetAddress> emails(Collection<String> emails) throws AddressException {
        if ((emails == null) || (emails.size() == 0)) {
            return null;
        }
        List<InternetAddress> addreses = new Vector<InternetAddress>();
        for (String email : emails) {
            addreses.add(new InternetAddress(email.trim()));
        }
        return addreses;
    }

    static boolean isEmptyList(List<InternetAddress> list) {
        if (list == null) {
            return true;
        } else {
            return list.isEmpty();
        }
    }

    static List<InternetAddress> forwardEmails(Collection<String> emails, String forwardAllTo) throws AddressException {
        if ((emails == null) || (emails.size() == 0)) {
            return null;
        }
        List<InternetAddress> addreses = new Vector<InternetAddress>();
        for (String email : emails) {
            try {
                if (email == null) {
                    throw new NullPointerException("Email Address is null");
                }
                addreses.add(new InternetAddress(forwardAllTo, email.trim(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
        }
        return addreses;
    }

    static boolean allowDestinations(String emailFilter, String email) {
        StringTokenizer st = new StringTokenizer(emailFilter, ";");
        if (st.hasMoreElements()) {
            while (st.hasMoreElements()) {
                if (email.endsWith(st.nextToken().trim())) {
                    return true;
                }
            }
            return false;
        } else {
            return email.endsWith(emailFilter);
        }
    }

    static List<InternetAddress> filterDestinations(String emailFilter, List<InternetAddress> list) {
        if (isEmptyList(list)) {
            return null;
        }
        List<InternetAddress> r = new Vector<InternetAddress>();
        for (InternetAddress a : list) {
            if (allowDestinations(emailFilter, a.getAddress())) {
                r.add(a);
            }
        }
        return r;
    }
}
