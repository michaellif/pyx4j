/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author michaellif
 */
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class BlockMixin extends Theme {

    public static enum StyleName implements IStyleName {
        PortalBlock
    }

    public BlockMixin() {
        Style style = new Style(".", StyleName.PortalBlock);
        style.addProperty("background", ThemeColor.foreground, 0.01);
        style.addProperty("border-color", ThemeColor.object1, 0.9);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-radius", "5px");
        style.addProperty("padding", "10px ");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}
