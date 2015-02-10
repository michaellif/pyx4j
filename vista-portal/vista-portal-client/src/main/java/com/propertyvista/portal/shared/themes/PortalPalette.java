/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2013
 * @author michaellif
 */
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;

public class PortalPalette extends VistaPalette {

    public PortalPalette(SiteDefinitionsDTO siteDefinitions) {
        super(new PortalBuilder(siteDefinitions));
    }

    protected PortalPalette(PortalBuilder builder) {
        super(builder);
    }

    public static class PortalBuilder extends Builder {

        public PortalBuilder(SiteDefinitionsDTO siteDefinitions) {
            super(siteDefinitions);
            switch (siteDefinitions.skin().getValue()) {
            case skin1:
                addColor(ThemeColor.object1, 100, 67)//
                        .addColor(ThemeColor.object2, 94, 60)//
                        .addColor(ThemeColor.contrast1, 94, 60)//
                        .addColor(ThemeColor.contrast2, 94, 60)//
                        .addColor(ThemeColor.contrast3, 94, 60)//
                        .addColor(ThemeColor.contrast4, 94, 60)//
                        .addColor(ThemeColor.contrast5, 94, 60)//
                        .addColor(ThemeColor.contrast6, 94, 60)//
                        .addColor(ThemeColor.foreground, 30, 20)//
                        .addColor(ThemeColor.formBackground, 30, 70)//
                        .addColor(ThemeColor.siteBackground, 100, 67);

                break;
            case skin2:
                addColor(ThemeColor.object1, 74, 56)//
                        .addColor(ThemeColor.object2, 74, 56)//
                        .addColor(ThemeColor.contrast1, 74, 56)//
                        .addColor(ThemeColor.contrast2, 74, 56)//
                        .addColor(ThemeColor.contrast3, 74, 56)//
                        .addColor(ThemeColor.contrast4, 74, 56)//
                        .addColor(ThemeColor.contrast5, 74, 56)//
                        .addColor(ThemeColor.contrast6, 74, 56)//
                        .addColor(ThemeColor.foreground, 30, 20)//
                        .addColor(ThemeColor.formBackground, 0, 100)//
                        .addColor(ThemeColor.siteBackground, 0, 100);
                break;
            case skin3:
                addColor(ThemeColor.object1, 0, 16)//
                        .addColor(ThemeColor.object2, 100, 29)//
                        .addColor(ThemeColor.contrast1, 100, 29)//
                        .addColor(ThemeColor.contrast2, 100, 29)//
                        .addColor(ThemeColor.contrast3, 100, 29)//
                        .addColor(ThemeColor.contrast4, 100, 29)//
                        .addColor(ThemeColor.contrast5, 100, 29)//
                        .addColor(ThemeColor.contrast6, 100, 29)//
                        .addColor(ThemeColor.foreground, 100, 29)//
                        .addColor(ThemeColor.formBackground, 0, 100)//
                        .addColor(ThemeColor.siteBackground, 0, 100);
                break;

            default:
                break;
            }

        }

        @Override
        public PortalPalette build() {
            return new PortalPalette(this);
        }
    }
}
