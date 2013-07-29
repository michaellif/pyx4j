/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class EntityViewTheme extends Theme {

    public static enum StyleName implements IStyleName {
        EntityView, EntityViewContent
    }

    public EntityViewTheme() {
        Style style = new Style(".", StyleName.EntityView);
        addStyle(style);

        style = new Style(".", StyleName.EntityViewContent);
        style.addProperty("background", ThemeColor.foreground, 0.01);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("border-style", "solid");
        style.addProperty("border-width", "1px");
        style.addProperty("border-radius", "5px");
        style.addProperty("padding", "10px");
        style.addProperty("margin", "10px");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
