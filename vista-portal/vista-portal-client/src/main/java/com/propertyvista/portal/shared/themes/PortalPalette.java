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
//        //Metcap
//        super(new Builder(siteDefinitions)//
//                .addColor(ThemeColor.object1, 100, 67)//
//                .addColor(ThemeColor.object2, 94, 60)//
//                .addColor(ThemeColor.contrast1, 78, 84)//
//                .addColor(ThemeColor.contrast2, 78, 84)//
//                .addColor(ThemeColor.contrast3, 78, 84)//
//                .addColor(ThemeColor.contrast4, 78, 84)//
//                .addColor(ThemeColor.contrast5, 78, 84)//
//                .addColor(ThemeColor.contrast6, 78, 84)//
//                .addColor(ThemeColor.foreground, 30, 20)//
//                .addColor(ThemeColor.formBackground, 30, 70)//
//                .addColor(ThemeColor.siteBackground, 100, 67)//
//        );

        //Timbercreek
        super(new Builder(siteDefinitions)//
                .addColor(ThemeColor.object1, 52, 67)//
                .addColor(ThemeColor.object2, 52, 67)//
                .addColor(ThemeColor.contrast1, 5, 64)//
                .addColor(ThemeColor.contrast2, 5, 64)//
                .addColor(ThemeColor.contrast3, 5, 64)//
                .addColor(ThemeColor.contrast4, 5, 64)//
                .addColor(ThemeColor.contrast5, 5, 64)//
                .addColor(ThemeColor.contrast6, 5, 64)//
                .addColor(ThemeColor.foreground, 30, 20)//
                .addColor(ThemeColor.formBackground, 0, 100)//
                .addColor(ThemeColor.siteBackground, 0, 100)//
        );

    }

}
