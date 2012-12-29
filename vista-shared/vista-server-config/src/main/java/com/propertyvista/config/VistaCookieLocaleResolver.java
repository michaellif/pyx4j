/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

import java.util.Arrays;
import java.util.Locale;

import com.pyx4j.i18n.server.CookieLocaleResolver;

public class VistaCookieLocaleResolver extends CookieLocaleResolver {

    @Override
    protected Locale getDefaultLocale() {
        if (VistaDeployment.isSystemNamespace()) {
            return Locale.ENGLISH;
        } else {
            return VistaLocale.getPmcDefaultEnglishLocale();
        }
    }

    @Override
    protected Iterable<Locale> getAvailableLocale() {
        if (VistaDeployment.isSystemNamespace()) {
            return Arrays.asList(Locale.ENGLISH);
        } else {
            return VistaLocale.getPmcLocale();
        }
    }

}
