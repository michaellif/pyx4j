/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2014
 * @author stanp
 */
package com.propertyvista.server.common.util;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.i18n.server.I18nManager;

import com.propertyvista.config.VistaLocale;
import com.propertyvista.domain.ILocalizedEntity;

public class LocalizedContent {

    public static <E extends ILocalizedEntity> E selectFromList(List<E> localizedList) {
        E result = null;
        String eng = VistaLocale.getPmcDefaultEnglishLocale().getLanguage();
        String lang = I18nManager.getThreadLocale().getLanguage();

        for (E cont : localizedList) {
            String contLang = cont.locale().getValue().getLanguage();
            if (lang.equals(contLang)) {
                result = cont;
                break;
            } else if (eng.equals(contLang)) {
                result = cont;
            }
        }

        return result;
    }

    public static <E extends ILocalizedEntity> List<E> selectAllFromList(List<E> localizedList) {
        List<E> result = new ArrayList<>();
        String lang = I18nManager.getThreadLocale().getLanguage();

        for (E cont : localizedList) {
            if (lang.equals(cont.locale().getValue().getLanguage())) {
                result.add(cont);
            }
        }

        if (result.isEmpty()) {
            String eng = VistaLocale.getPmcDefaultEnglishLocale().getLanguage();

            for (E cont : localizedList) {
                if (eng.equals(cont.locale().getValue().getLanguage())) {
                    result.add(cont);
                }
            }
        }

        return result;
    }
}
