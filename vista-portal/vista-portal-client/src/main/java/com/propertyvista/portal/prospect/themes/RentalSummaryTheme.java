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
package com.propertyvista.portal.prospect.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

public class RentalSummaryTheme extends Theme {

    public static enum StyleName implements IStyleName {
        RentalSummaryBlock, RentalSummaryCaption
    }

    public RentalSummaryTheme() {
        Style style = new Style(".", StyleName.RentalSummaryBlock);
        style.addProperty("font-weight", "bolder");
        style.addProperty("font-size", "0.8em");
        style.addProperty("padding-top", "2px");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.RentalSummaryCaption);
        style.addProperty("padding-top", "5px");
        style.addProperty("font-style", "italic");
        style.addProperty("font-size", "0.9em");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
