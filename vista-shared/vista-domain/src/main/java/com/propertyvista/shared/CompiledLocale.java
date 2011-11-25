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
public enum CompiledLocale implements Serializable {

    @Translate(value = "English")
    en,

    @Translate(value = "English (Canada)")
    en_CA,

    @Translate(value = "English (United States)")
    en_US,

    @Translate(value = "French")
    fr,

    @Translate(value = "French (Canada)")
    fr_CA,

    @Translate(value = "Spanish")
    sp,

    @Translate(value = "Russian")
    ru;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
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
