/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.i18n.server.I18nManager;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.shared.CompiledLocale;

public class PMSiteWebRequest extends ServletWebRequest {

    private final PMSiteContentManager contentManager;

    private final StylesheetTemplateModel stylesheetTemplateModel;

    private AvailableLocale siteLocale;

    public PMSiteWebRequest(HttpServletRequest httpServletRequest, String filterPrefix) {
        super(httpServletRequest, filterPrefix);

        // get PMContentManager with site descr separate (if changed update it)

        final String cacheKey = "pm-site";

        PMSiteContentManager cm = (PMSiteContentManager) CacheService.get(cacheKey);
        if (cm == null) {
            cm = new PMSiteContentManager();
            CacheService.put(cacheKey, cm);
        } else {
            if (cm.refresh()) {
                CacheService.put(cacheKey, cm);
            }
        }
        contentManager = cm;

        stylesheetTemplateModel = new StylesheetTemplateModel(this);
    }

    public StylesheetTemplateModel getStylesheetTemplateModel() {
        return stylesheetTemplateModel;
    }

    public PMSiteContentManager getContentManager() {
        return contentManager;
    }

    public void setDefaultLocale() {
        setSiteLocale(null);
    }

    public void setSiteLocale(String lang) {
        AvailableLocale locale = null;
        if (lang != null) {
            locale = getRequestPathLocale(lang, contentManager.getAllAvailableLocale());
        }
        if (locale == null) {
            // this will return either current or fist available locale
            locale = getCurrentOrDefaultLocale(contentManager.getAllAvailableLocale());
        }
        I18nManager.setThreadLocale(getLocale(locale.lang().getValue()));
        siteLocale = locale;
        // need locale on the client for GWT modules ?
        ((WebResponse) RequestCycle.get().getResponse()).addCookie(new Cookie("locale", siteLocale.lang().getValue().name()));
    }

    public AvailableLocale getSiteLocale() {
        if (siteLocale == null) {
            setDefaultLocale();
        }
        return siteLocale;
    }

    private AvailableLocale getRequestPathLocale(String localePath, List<AvailableLocale> allAvailableLocale) {
        if (localePath == null) {
            return null;
        }
        for (AvailableLocale l : allAvailableLocale) {
            if (localePath.equals(l.lang().getValue().name())) {
                return l;
            }
        }
        return null;
    }

    private AvailableLocale getCurrentOrDefaultLocale(List<AvailableLocale> allAvailableLocale) {
        Locale locale = I18nManager.getThreadLocale();
        try {
            CompiledLocale lang = CompiledLocale.valueOf(locale.getLanguage() + "_" + locale.getCountry());
            for (AvailableLocale l : allAvailableLocale) {
                if (lang.equals(l.lang().getValue())) {
                    return l;
                }
            }
        } catch (IllegalArgumentException ignore) {
        }

        for (AvailableLocale l : allAvailableLocale) {
            if (locale.getLanguage().equals(l.lang().getValue().name())) {
                return l;
            }
        }
        if (allAvailableLocale.size() == 0) {
            throw new Error("Accessing Empty DB");
        } else {
            // Locale not found, select the first one.
            return allAvailableLocale.get(0);
        }
    }

    protected Locale getLocale(CompiledLocale cl) {
        switch (cl) {
        case en:
            return Locale.ENGLISH;
        case fr:
            return Locale.FRENCH;
        case ru:
            return new Locale("ru", "RU");
        default:
            return Locale.ENGLISH;
        }
    }
}
