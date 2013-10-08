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

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public abstract class DefaultWidgetDecoratorTheme extends Theme {

    public static enum StyleName implements IStyleName {
        WidgetDecorator, WidgetDecoratorLabelHolder, WidgetDecoratorLabel, WidgetDecoratorMandatoryImage, WidgetDecoratorInfoImage,

        WidgetDecoratorContentPanel, WidgetDecoratorComponent, WidgetDecoratorComponentHolder,

        EntityContainerDecorator, EntityContainerDecoratorCollapsedCaption
    }

    public static enum StyleDependent implements IStyleDependent {
        readOnly, noMandatoryStar, invalid, viewable, verticalAlign, labelAlignRight, labelAlignCenter, componentAlignRight, componentAlignCenter
    }

    public DefaultWidgetDecoratorTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        Style style = new Style(".", StyleName.WidgetDecorator);
        style.addProperty("border-spacing", "0");
        style.addProperty("padding", "5px 0");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.viewable);
        style.addProperty("font-weight", "bold");
        style.addProperty("color", ThemeColor.foreground, 1);
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorLabelHolder);
        style.addProperty("text-align", "left");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponent);
        style.addProperty("white-space", "normal");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.verticalAlign, " .", StyleName.WidgetDecoratorLabelHolder);
        style.addProperty("padding-right", "0");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorLabelHolder, "-", StyleDependent.labelAlignCenter);
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorLabelHolder, "-", StyleDependent.labelAlignRight);
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.verticalAlign, " .", StyleName.WidgetDecoratorLabelHolder, "-",
                StyleDependent.labelAlignRight);
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorLabel);
        style.addProperty("color", ThemeColor.foreground, 1);
        style.addProperty("font-weight", "bold");
        style.addProperty("padding-right", "1em");
        style.addProperty("white-space", "normal");
        style.addProperty("line-height", "2em");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponent, " .", CComponentTheme.StyleName.FieldEditorPanel);
        style.addProperty("line-height", "2em");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.viewable, " .", StyleName.WidgetDecoratorLabel);
        style.addProperty("color", ThemeColor.foreground, 0.5);
        style.addProperty("font-size", "0.9em");
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponent, " .", CComponentTheme.StyleName.FieldViewerPanel);
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorInfoImage);
        style.addProperty("padding-left", "5px");
        style.addProperty("line-height", "0");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorMandatoryImage);
        style.addProperty("position", "absolute");
        style.addProperty("left", "-7px");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecorator, "-", StyleDependent.noMandatoryStar, " .", StyleName.WidgetDecoratorMandatoryImage, " ", "img");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorContentPanel);
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponentHolder);
        style.addProperty("word-wrap", "break-word");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponent);
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponent, "-", StyleDependent.invalid, " .", DefaultWidgetsTheme.StyleName.TextBox);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "#f79494");
        style.addProperty("background-color", "#f8d8d8");
        addStyle(style);

        style = new Style(".", StyleName.WidgetDecoratorComponent, "-", StyleDependent.invalid, " .", DefaultWidgetsTheme.StyleName.ListBox);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", "#f79494");
        style.addProperty("background-color", "#f8d8d8");
        addStyle(style);

        style = new Style(".", StyleName.EntityContainerDecoratorCollapsedCaption);
        style.addProperty("font-size", "1.1em");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.EntityContainerDecorator);
        style.addProperty("margin", "6px 0px");
        style.addProperty("border", "dotted 1px");
        style.addProperty("border-color", getBackgroundColor());
        style.addProperty("min-height", "22px");
        addStyle(style);

        style = new Style(".", StyleName.EntityContainerDecorator, ":hover");
        style.addProperty("border", "solid 1px");
        style.addProperty("border-color", getBackgroundColor());
        addStyle(style);

    }

    protected abstract ThemeColor getBackgroundColor();
}
