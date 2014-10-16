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
package com.propertyvista.crm.client.themes;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.common.client.theme.VistaPalette;

public class CrmNoServerPalette extends VistaPalette {

    public CrmNoServerPalette() {
        super();
        putThemeColor(ThemeColor.object1, "#315EAF");
        putThemeColor(ThemeColor.object2, "B26C1F");
        putThemeColor(ThemeColor.contrast1, "red");
        putThemeColor(ThemeColor.contrast2, "orange");
        putThemeColor(ThemeColor.formBackground, "#fefefe");
        putThemeColor(ThemeColor.foreground, "#333333");
    }

}
