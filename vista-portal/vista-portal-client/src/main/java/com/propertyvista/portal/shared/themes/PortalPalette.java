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
 * @version $Id$
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
                        .addColor(ThemeColor.contrast1, 78, 84)//
                        .addColor(ThemeColor.contrast2, 78, 84)//
                        .addColor(ThemeColor.contrast3, 78, 84)//
                        .addColor(ThemeColor.contrast4, 78, 84)//
                        .addColor(ThemeColor.contrast5, 78, 84)//
                        .addColor(ThemeColor.contrast6, 78, 84)//
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
