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

import com.pyx4j.commons.css.ColorUtil;
import com.pyx4j.commons.css.ThemeColors;

import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.SitePalette;

public class PortalPalette extends VistaPalette {

    public PortalPalette(SiteDescriptor descriptor) {

        SitePalette palette = descriptor.sitePalette();

        Skin skin = descriptor.skin().getValue();

        putThemeColor(ThemeColors.object1, ColorUtil.hsbToRgb((float) palette.object1().getValue() / 360, (float) skin.getColorProperties()[0] / 100,
                (float) skin.getColorProperties()[1] / 100));
        putThemeColor(ThemeColors.object2, ColorUtil.hsbToRgb((float) palette.object2().getValue() / 360, (float) skin.getColorProperties()[2] / 100,
                (float) skin.getColorProperties()[3] / 100));
        putThemeColor(ThemeColors.contrast1, ColorUtil.hsbToRgb((float) palette.contrast1().getValue() / 360, (float) skin.getColorProperties()[4] / 100,
                (float) skin.getColorProperties()[5] / 100));
        putThemeColor(ThemeColors.contrast2, ColorUtil.hsbToRgb((float) palette.contrast2().getValue() / 360, (float) skin.getColorProperties()[6] / 100,
                (float) skin.getColorProperties()[7] / 100));
        putThemeColor(ThemeColors.foreground, ColorUtil.hsbToRgb((float) palette.background().getValue() / 360, (float) skin.getColorProperties()[8] / 100,
                (float) skin.getColorProperties()[9] / 100));
        putThemeColor(
                ThemeColors.background,
                ColorUtil.hsbToRgb((float) palette.foreground().getValue() / 360, (float) skin.getColorProperties()[10] / 100,
                        (float) skin.getColorProperties()[11] / 100));

    }
}
