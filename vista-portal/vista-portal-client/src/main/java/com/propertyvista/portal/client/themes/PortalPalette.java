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
import com.propertyvista.domain.site.SitePalette;

public class PortalPalette extends VistaPalette {

    public PortalPalette(SitePalette palette) {

        putThemeColor(ThemeColors.object1, palette.object1().getValue());
        putThemeColor(ThemeColors.object2, palette.object2().getValue());
        putThemeColor(ThemeColors.contrast1, palette.contrast1().getValue());
        putThemeColor(ThemeColors.contrast2, palette.contrast2().getValue());
        putThemeColor(ThemeColors.background, palette.background().getValue());
        putThemeColor(ThemeColors.foreground, palette.foreground().getValue());

    }
}
