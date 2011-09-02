/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

public interface Locale extends IEntity {

    @Translatable
    public enum Lang {

        @Translation(value = "English")
        en,

        @Translation(value = "French")
        fr;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @NotNull
    @ToString(index = 0)
    IPrimitive<Lang> lang();
}
