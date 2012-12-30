/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-29
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.shared.i18n.CompiledLocale;

public class VistaLocale {

    public static final Locale RU = new Locale("ru", "RU");

    public static final Locale ES = new Locale("es");

    private static final List<Locale> locale_CA = getLocaleCA();

    private static final List<Locale> locale_US = getLocaleUS();

    private static List<Locale> getLocaleCA() {
        return Arrays.asList(Locale.CANADA, Locale.FRENCH, RU, ES, Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE);
    }

    private static List<Locale> getLocaleUS() {
        return Arrays.asList(Locale.US, Locale.FRENCH, RU, ES, Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE);
    }

    public static List<Locale> getPmcLocale() {
        switch (VistaFeatures.instance().countryOfOperation()) {
        case Canada:
            return locale_CA;
        case US:
            return locale_US;
        case UK:
            return Arrays.asList(Locale.UK);
        default:
            // Default fallback
            return Arrays.asList(Locale.ENGLISH);
        }

    }

    public static Locale getPmcDefaultEnglishLocale() {
        switch (VistaFeatures.instance().countryOfOperation()) {
        case Canada:
            return Locale.CANADA;
        case US:
            return Locale.US;
        case UK:
            return Locale.UK;
        default:
            return Locale.ENGLISH;
        }
    }

    public static Locale toPmcLocale(CompiledLocale cl) {
        switch (cl) {
        // TODO The English version selection may not be necessary once currency implemented properly
        case en_GB:
        case en_US:
        case en_CA:
        case en:
            return getPmcDefaultEnglishLocale();
        default:
            return toLocale(cl);
        }
    }

    public static Locale toLocale(CompiledLocale cl) {
        switch (cl) {
        case en_GB:
            return Locale.UK;
        case en_US:
            return Locale.US;
        case en_CA:
            return Locale.CANADA;
        case en:
            return Locale.ENGLISH;
        case fr:
            return Locale.FRENCH;
        case ru:
            return RU;
        case es:
            return ES;
        case zh_CN:
            return Locale.SIMPLIFIED_CHINESE;
        case zh_TW:
            return Locale.TRADITIONAL_CHINESE;
        default:
            return Locale.ENGLISH;
        }
    }
}
