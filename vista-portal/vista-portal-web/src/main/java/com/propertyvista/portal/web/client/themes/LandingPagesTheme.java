/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

/**
 * Defines style names and CSS for the public portal pages as login, sign-up, password reset etc...
 */
public class LandingPagesTheme extends Theme {

    public static enum StyleName implements IStyleName {
        //@formatter:off

        
        LoginImageDecoration,
        LoginImageDecorationImage,
        LoginImageDecorationLabel,
        

        //@formatter:on

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
