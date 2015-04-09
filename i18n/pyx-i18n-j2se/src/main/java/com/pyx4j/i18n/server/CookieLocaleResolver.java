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
 * Created on Oct 17, 2011
 * @author vlads
 */
package com.pyx4j.i18n.server;

import java.util.Arrays;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.pyx4j.config.server.LocaleResolver;

public class CookieLocaleResolver implements LocaleResolver {

    static public final Locale RU = new Locale("ru", "RU");

    public static String COOKIE_NAME = "locale";

    protected Iterable<Locale> getAvailableLocale() {
        return Arrays.asList(Locale.ENGLISH, Locale.FRENCH, RU);
    }

    protected Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public Locale getRequestLocale(HttpServletRequest httprequest) {
        String localeCookie = null;
        Cookie[] cookies = httprequest.getCookies();
        if (cookies == null) {
            return getDefaultLocale();
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                localeCookie = cookie.getValue();
                break;
            }
        }
        // Select Locale
        Locale languageMatch = null;
        if (localeCookie != null) {
            String language;
            String country;

            String[] localeParts = localeCookie.split("_");
            if (localeParts.length > 1) {
                language = localeParts[0].toLowerCase(Locale.ENGLISH);
                country = localeParts[1].toLowerCase(Locale.ENGLISH);
            } else {
                language = localeCookie.toLowerCase(Locale.ENGLISH);
                country = "";
            }

            for (Locale l : getAvailableLocale()) {
                if (language.equals(l.getLanguage())) {
                    if (languageMatch == null) {
                        languageMatch = l;
                    }
                    if (country.equalsIgnoreCase(l.getCountry())) {
                        return l;
                    }
                }
            }
        }

        if (languageMatch != null) {
            return languageMatch;
        } else {
            return getDefaultLocale();
        }
    }

    public static String getCurrentLocaleCookieValue() {
        Locale locale = I18nManager.getThreadLocale();
        if (locale.getCountry() != null) {
            return locale.getLanguage() + "_" + locale.getCountry();
        } else {
            return locale.getLanguage();
        }
    }
}
