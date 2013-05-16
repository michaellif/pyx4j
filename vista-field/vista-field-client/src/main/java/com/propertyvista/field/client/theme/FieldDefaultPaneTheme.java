/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.theme;

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.site.client.ui.DefaultPaneTheme;

public class FieldDefaultPaneTheme extends DefaultPaneTheme {

    @Override
    protected void initListerStyles() {
        Style style = new Style(".", StyleName.Lister);
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", StyleName.ListerFiltersPanel);
        style.addProperty("background-color", ThemeColor.foreground, 0.05);
        style.addProperty("padding-top", "0.5em");
        addStyle(style);

        style = new Style(".", StyleName.ListerListPanel);
        addStyle(style);

        style = new Style(".", StyleName.ListerListPanel, " .", DefaultDataTableTheme.StyleName.DataTable);
        style.addProperty("border-left", "1px solid");
        style.addProperty("border-right", "1px solid");
        style.addProperty("border-left-color", ThemeColor.foreground, 0.4);
        style.addProperty("border-right-color", ThemeColor.foreground, 0.4);
        addStyle(style);
    }

}
