/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;

public class CommunicationTheme extends Theme {

    public enum StyleName implements IStyleName {

        CommunicationFolderView, CommunicationThreadName

    }

    public CommunicationTheme() {
        initTheme();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    public void initTheme() {
        {
            Style style = new Style(".", CommunicationTheme.StyleName.CommunicationFolderView.name(), " .",
                    WidgetDecoratorTheme.StyleName.EntityContainerDecoratorCollapsedCaption);
            style.addProperty("font-size", "0.9em");
            addStyle(style);
        }
        {
            Style style = new Style(".", CommunicationTheme.StyleName.CommunicationThreadName.name());
            style.addProperty("font-size", "1.2em");
            style.addProperty("font-weight", "bold");
            addStyle(style);
        }
    }

}
