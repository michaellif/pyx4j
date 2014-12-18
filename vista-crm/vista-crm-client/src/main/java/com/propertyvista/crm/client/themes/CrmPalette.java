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
 */
package com.propertyvista.crm.client.themes;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.portal.rpc.portal.SiteDefinitionsDTO;

public class CrmPalette extends VistaPalette {

    public CrmPalette(SiteDefinitionsDTO siteDefinitions) {
        super(new Builder(siteDefinitions)//
                .addColor(ThemeColor.object1, 30, 70)//
                .addColor(ThemeColor.object2, 30, 60)//
                .addColor(ThemeColor.contrast1, 100, 100)//
                .addColor(ThemeColor.contrast2, 100, 100)//
                .addColor(ThemeColor.foreground, 0, 40)//
                .addColor(ThemeColor.formBackground, 0, 100)//
        );
    }

}
