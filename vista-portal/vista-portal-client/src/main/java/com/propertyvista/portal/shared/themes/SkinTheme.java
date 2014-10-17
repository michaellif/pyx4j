/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 17, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.domain.site.SiteDescriptor.Skin;

public class SkinTheme extends Theme {

    public SkinTheme(Skin skin) {

    }

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
