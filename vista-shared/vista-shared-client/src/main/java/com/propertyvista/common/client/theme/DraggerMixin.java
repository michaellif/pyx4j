/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 28, 2011
 * @author michaellif
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class DraggerMixin extends Theme {

    public DraggerMixin() {
        Style style = new Style(".gwt-SplitLayoutPanel-HDragger");
        style.addProperty("background", ThemeColor.object1, 1);
        style.addProperty("cursor", "col-resize");
        addStyle(style);

        style = new Style(".gwt-SplitLayoutPanel-VDragger");
        style.addProperty("background", ThemeColor.object1, 1);
        style.addProperty("cursor", "row-resize");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
