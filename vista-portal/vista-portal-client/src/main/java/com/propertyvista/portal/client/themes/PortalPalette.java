/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-02
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.themes;

import com.pyx4j.commons.css.ThemeColors;

import com.propertyvista.common.client.theme.VistaPalette;

public class PortalPalette extends VistaPalette {

    public PortalPalette() {

        putThemeColor(ThemeColors.object1, "#666666");
        putThemeColor(ThemeColors.object2, "#666666");
        putThemeColor(ThemeColors.contrast1, "red");
        putThemeColor(ThemeColors.contrast2, "orange");
        putThemeColor(ThemeColors.background, "#fefefe");
        putThemeColor(ThemeColors.foreground, "#666666");
        putThemeColor(ThemeColors.form, "#666666");

    }
}
