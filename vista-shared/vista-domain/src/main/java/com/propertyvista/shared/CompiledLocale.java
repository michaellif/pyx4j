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

import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translation;

//TODO Use compiler do define available in compiled GWT locales  
public enum CompiledLocale implements Serializable {

    @Translation(value = "English")
    en,

    @Translation(value = "English (Canada)")
    en_CA,

    @Translation(value = "English (United States)")
    en_US,

    @Translation(value = "French")
    fr,

    @Translation(value = "French (Canada)")
    fr_CA,

    @Translation(value = "Spanish")
    sp,

    @Translation(value = "Russian")
    ru;

    @Override
    public String toString() {
        return I18nEnum.tr(this);
    }

}
