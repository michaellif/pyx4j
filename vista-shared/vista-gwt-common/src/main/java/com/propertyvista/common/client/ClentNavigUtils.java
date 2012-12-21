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
import java.util.List;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.shared.CompiledLocale;
import com.propertyvista.shared.config.VistaFeatures;

public class ClentNavigUtils {

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
            for (String localeName : LocaleInfo.getAvailableLocaleNames()) {
                if (localeName.equals("default")) {
                    localeName = "en_US";
                }
                CompiledLocale cl = CompiledLocale.valueOf(localeName);
                if (DemoData.vistaDemo && cl == CompiledLocale.ru) {
                    continue;
                }
                if (!locales.contains(cl)) {
                    locales.add(cl);
                }
            }
        }
        return locales;
    }

    public static CompiledLocale getCurrentLocale() {
        String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        if (currentLocale.equals("default")) {
            currentLocale = "en_US";
        }
        return CompiledLocale.valueOf(currentLocale);
    }

    public static void setCountryOfOperationLocale() {
        CompiledLocale compiledLocale = getCurrentLocale();
        if ((VistaFeatures.instance().countryOfOperation() == CountryOfOperation.UK) && (compiledLocale != CompiledLocale.en_GB)) {
            if (isLocaleAvailable(CompiledLocale.en_GB)) {
                ClentNavigUtils.changeApplicationLocale(CompiledLocale.en_GB);
            } else if (ApplicationMode.isDevelopment()) {
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
