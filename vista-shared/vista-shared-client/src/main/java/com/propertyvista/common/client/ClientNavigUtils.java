/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.shared.i18n.CompiledLocale;

public class ClientNavigUtils {

    /**
     * Used for inter-modules redirections.
     * Consider http://localhost:8888/vista/ and http://localhost:8888/vista/index.html
     */
    public static String getDeploymentBaseURL() {
        return NavigationUri.getDeploymentBaseURL();
    }

    public static List<CompiledLocale> obtainAvailableLocales() {
        List<CompiledLocale> locales = new ArrayList<CompiledLocale>();
        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.UK) {
            locales.add(CompiledLocale.en_GB);
        } else {
            EnumSet<CompiledLocale> SupportedLocales = CompiledLocale.getSupportedLocales();
            for (String localeName : LocaleInfo.getAvailableLocaleNames()) {
                if (localeName.equals("default")) {
                    localeName = "en";
                }
                CompiledLocale cl = CompiledLocale.valueOf(localeName);
                if (SupportedLocales.contains(cl)) {
                    if (!locales.contains(cl)) {
                        locales.add(cl);
                    }
                } else if (cl == CompiledLocale.en) {
                    locales.add(getDefaultLocaleByCountryOfOperation());
                }
            }

            switch (VistaFeatures.instance().countryOfOperation()) {
            case Canada:
                locales.remove(CompiledLocale.en_US);
                locales.remove(CompiledLocale.en_GB);
                break;
            case US:
                locales.remove(CompiledLocale.en_CA);
                locales.remove(CompiledLocale.en_GB);
                break;
            case UK:
                locales.remove(CompiledLocale.en_CA);
                locales.remove(CompiledLocale.en_US);
                break;
            default:
                break;
            }
        }
        return locales;
    }

    public static CompiledLocale getDefaultLocaleByCountryOfOperation() {
        switch (VistaFeatures.instance().countryOfOperation()) {
        case Canada:
            return CompiledLocale.en_CA;
        case US:
            return CompiledLocale.en_US;
        case UK:
            return CompiledLocale.en_GB;
        default:
            return getCurrentLocale();
        }
    }

    public static CompiledLocale getCurrentLocale() {
        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        if (currentLocale.equals("default")) {
            currentLocale = "en_US";
        }
        return CompiledLocale.valueOf(currentLocale);
    }

    public static void setCountryOfOperationLocale() {
        CompiledLocale currentLocale = getCurrentLocale();
        CompiledLocale defaultLocale = getDefaultLocaleByCountryOfOperation();

        if ((defaultLocale != currentLocale) && (currentLocale.getLanguage().equals(CompiledLocale.en.name()))) {
            if (isLocaleAvailable(defaultLocale)) {
                ClientNavigUtils.changeApplicationLocale(defaultLocale);
            } else if (ApplicationMode.isDevelopment() && (defaultLocale != CompiledLocale.en_CA)) {
                MessageDialog.warn("Warning", "(dev) This PMC Locale was not compiled in this development versions");
            }
        }

    }

    public static boolean isLocaleAvailable(CompiledLocale locale) {
        for (String localeName : LocaleInfo.getAvailableLocaleNames()) {
            if (localeName.equals("default")) {
                localeName = "en_US";
            }
            if (localeName.equals(locale.name())) {
                return true;
            }
        }
        return false;
    }

    public static void changeApplicationLocale(CompiledLocale locale) {
        UrlBuilder builder = Window.Location.createUrlBuilder().setParameter("locale", locale.name());
        Window.Location.replace(builder.buildString());
    }
}
