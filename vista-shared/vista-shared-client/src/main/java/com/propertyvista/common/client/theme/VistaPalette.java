/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 15, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.css.ColorUtil;
import com.pyx4j.commons.css.Palette;
import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;

public class VistaPalette extends Palette {

    protected VistaPalette() {
    }

    protected VistaPalette(Builder builder) {
        for (ThemeColor themeColor : builder.themeColors.keySet()) {
            putThemeColor(themeColor, builder.themeColors.get(themeColor));
        }
    }

    public static class Builder {

        private final SiteDefinitionsDTO siteDefinitions;

        private final Map<ThemeColor, Integer> themeColors;

        public Builder(SiteDefinitionsDTO siteDefinitions) {
            this.siteDefinitions = siteDefinitions;
            themeColors = new HashMap<ThemeColor, Integer>();
        }

        public VistaPalette build() {
            return new VistaPalette(this);
        }

        public Builder addColor(ThemeColor color, int saturation, int brightness) {
            Integer hue = 0;
            switch (color) {
            case object1:
                hue = siteDefinitions.palette().object1().getValue();
                break;
            case object2:
                hue = siteDefinitions.palette().object2().getValue();
                break;
            case contrast1:
                hue = siteDefinitions.palette().contrast1().getValue();
                break;
            case contrast2:
                hue = siteDefinitions.palette().contrast2().getValue();
                break;
            case contrast3:
                hue = siteDefinitions.palette().contrast3().getValue();
                break;
            case contrast4:
                hue = siteDefinitions.palette().contrast4().getValue();
                break;
            case contrast5:
                hue = siteDefinitions.palette().contrast5().getValue();
                break;
            case contrast6:
                hue = siteDefinitions.palette().contrast6().getValue();
                break;
            case foreground:
                hue = siteDefinitions.palette().foreground().getValue();
                break;
            case formBackground:
                hue = siteDefinitions.palette().formBackground().getValue();
                break;
            case siteBackground:
                hue = siteDefinitions.palette().siteBackground().getValue();
                break;
            default:
                break;

            }

            themeColors.put(color, ColorUtil.hsbToRgb((float) hue / 360, (float) saturation / 100, (float) brightness / 100));
            return this;
        }
    }

}
