/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-07
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface SiteDescriptor extends IEntity {

    @Translatable
    public enum Skin {

        skin1,

        skin2,

        skin3;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    IList<Locale> locales();

    @NotNull
    @ToString(index = 0)
    IPrimitive<Skin> skin();

    //color picker
    IPrimitive<String> baseColor();

    IPrimitive<String> copyright();

    IList<Resource> logo();

    IList<Resource> slogan();

    @Owned
    @Caption(name = "Child Pages:")
    IList<PageDescriptor> childPages();

    // Image for landing page
    @Owned
    IList<Resource> images();

}
