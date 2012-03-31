/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.shared;

import java.io.Serializable;

import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

//TODO Use compiler do define available in compiled GWT locales
// Use http://www.cylog.org/tools/utf8_converter.jsp to avoid UTF-8 format in this file
public enum CompiledLocale implements Serializable {

    @Translate(value = "English")
    en("English"),

//    @Translate(value = "English (Canada)")
//    en_CA("English"),
//
//    @Translate(value = "English (US)")
//    en_US("English"),

    @Translate(value = "French")
    fr("Fran\u00e7ais"),

//    @Translate(value = "French (Canada)")
//    fr_CA("Fran\u00e7ais"),

    @Translate(value = "Spanish")
    es("Espa\u00f1ol"),

    @Translate(value = "Russian")
    ru("\u0420\u0443\u0441\u0441\u043a\u0438\u0439"),

    @Translate(value = "Simplified Chinese")
    zh_CN("\u7b80\u4f53\u4e2d\u6587"),

    @Translate(value = "Traditional Chinese")
    zh_TW("\u7e41\u9ad4\u4e2d\u6587");

    private final String nativeDisplayName;

    CompiledLocale(String nativeDisplayName) {
        this.nativeDisplayName = nativeDisplayName;
    }

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }

    public String getNativeDisplayName() {
        return nativeDisplayName;
    }

    public String getLanguage() {
        int d = this.name().indexOf('_');
        if (d == -1) {
            return this.name();
        } else {
            return this.name().substring(0, d);
        }
    }
}
