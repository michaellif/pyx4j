/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins.base;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.ThemeId;

import com.propertyvista.pmsite.server.skins.PMSiteThemeBase;

public class DefaultAptDetailsTheme extends PMSiteThemeBase {

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    @Override
    public void initStyle() {
        // TODO Auto-generated method stub

    }
}
