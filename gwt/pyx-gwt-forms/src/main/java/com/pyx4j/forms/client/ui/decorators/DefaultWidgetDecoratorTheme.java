/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class DefaultWidgetDecoratorTheme extends Theme {

    public static enum StyleName implements IStyleName {
        WidgetDecorator, WidgetDecoratorLabelHolder, WidgetDecoratorLabel, WidgetDecoratorMandatoryImage, WidgetDecoratorInfoImage,

        WidgetDecoratorContentPanel, WidgetDecoratorComponent, WidgetDecoratorComponentHolder
    }

    public static enum StyleDependent implements IStyleDependent {
        readOnly, noMandatoryStar
    }

    public DefaultWidgetDecoratorTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.WidgetDecorator);
        style.addProperty("border-spacing", "0");
        style.addProperty("padding", "0.2em 0");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.readOnly);
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColors.foreground, 1);
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorLabelHolder);
        style.addProperty("padding-right", "1em");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorLabel);
        style.addProperty("color", ThemeColors.foreground, 1);
        style.addProperty("font-weight", "bold");
        style.addProperty("display", "inline");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.readOnly, " .", StyleName.WidgetDecoratorLabel);
        style.addProperty("color", ThemeColors.foreground, 0.5);
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorInfoImage);
        style.addProperty("padding-left", "5px");
        style.addProperty("line-height", "0");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorMandatoryImage);
        style.addProperty("display", "inline-block");
        style.addProperty("width", "5px");
        style.addProperty("padding-right", "3px");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.noMandatoryStar, " .", StyleName.WidgetDecoratorMandatoryImage, " ", "img");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorContentPanel);
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponentHolder);
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponent);
        addStyle(style);

    }
}
