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
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

public class StepsTheme extends Theme {

    public enum StyleName implements IStyleName {
        WizardStepPanel, WizardStepHandler, WizardStepHandlerCaption
    }

    public StepsTheme() {
        Style style = new Style(".", StepsTheme.StyleName.WizardStepPanel.name());
        style.addProperty("text-align", "center");
        style.addProperty("padding", "10px 0 0");
        addStyle(style);

        style = new Style(".", StepsTheme.StyleName.WizardStepHandler.name());
        style.addProperty("font-style", "italic");
        style.addProperty("color", "white");
        addStyle(style);

        style = new Style(".", StepsTheme.StyleName.WizardStepHandlerCaption.name());
        style.addProperty("background", "#aaa");
        style.addProperty("color", "#fff");
        style.addProperty("margin", "5px");
        style.addProperty("padding", "2px 10px");
        style.addProperty("border-radius", "4px");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
