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
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.pmsite.server.skins;

import com.propertyvista.pmsite.server.skins.base.PMSiteTheme;

public class PMSiteStyleFactory {

    public static PMSiteThemeBase create(Class<? extends PMSiteTheme> themeClass, PMSiteTheme.Stylesheet style) {
        PMSiteThemeBase styleTheme = null;
        try {
            try {
                String clsName = themeClass.getPackage().getName() + "." + style.name() + "Theme";
                styleTheme = (PMSiteThemeBase) Class.forName(clsName).newInstance();
            } catch (ClassNotFoundException notFound) {
                String clsName = PMSiteTheme.class.getPackage().getName() + "." + "Default" + style.name() + "Theme";
                styleTheme = (PMSiteThemeBase) Class.forName(clsName).newInstance();
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return styleTheme;
    }
}
