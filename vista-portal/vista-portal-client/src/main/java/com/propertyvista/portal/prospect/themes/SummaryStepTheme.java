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
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class SummaryStepTheme extends Theme {

    public static enum StyleName implements IStyleName {
        SummaryStepForm, SummaryStepSection, SummaryStepSectionCaptionBar, SummaryStepSectionStatus, SummaryStepSectionCaption
    }

    public SummaryStepTheme() {
        Style style = new Style(".", StyleName.SummaryStepForm);
        addStyle(style);

        style = new Style(".", StyleName.SummaryStepSection);
        style.addProperty("line-height", "2em");
        style.addProperty("margin", "6px 0");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.SummaryStepSection, " .", DefaultWidgetsTheme.StyleName.CollapsablePanelImage);
        style.addProperty("height", "50px");
        addStyle(style);

        style = new Style(".", StyleName.SummaryStepSectionCaptionBar);
        style.addProperty("height", "40px");
        style.addProperty("line-height", "40px");
        style.addProperty("margin", "2px -10px");
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("padding", "5px;");
        addStyle(style);

        style = new Style(".", StyleName.SummaryStepSectionStatus);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("position", "absolute");
        style.addProperty("right", "0");
        style.addProperty("margin", "4px 20px 0 0");
        addStyle(style);

        style = new Style(".", StyleName.SummaryStepSectionCaption);
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        style.addProperty("padding-left", "40px");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
