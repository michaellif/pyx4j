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
 * Created on Nov 14, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class DefaultCComponentsTheme extends Theme {

    public static enum StyleName implements IStyleName {
        Editor, EditorPanel, Viewer, ViewerPanel, ValidationLabel, NoteLabel
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, readonly, info, anchor, warning
    }

    public DefaultCComponentsTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        initTextBoxStyle();
        initListBoxStyle();
        initTriggerButtonStyle();
        initValidationLabelStyle();
        initNoteStyle();
        initViewerHyperlinkStyle();
    }

    private void initViewerHyperlinkStyle() {
        Style style = new Style(".", StyleName.Viewer, "-", StyleDependent.anchor);
        style.addProperty("border-bottom", "1px solid");
        style.addProperty("border-bottom-color", ThemeColor.foreground, 0.6);
        style.addProperty("cursor", "pointer");
        addStyle(style);
    }

    protected void initTextBoxStyle() {

        Style style = new Style(".", DefaultWidgetsTheme.StyleName.TextBox, "-", StyleDependent.disabled);
        style.addProperty("background-color", ThemeColor.foreground, 0.1);
        style.addProperty("color", ThemeColor.foreground, 0.6);
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.TextBox, "-", StyleDependent.readonly);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.1);
        style.addProperty("background-color", ThemeColor.foreground, 0);
        addStyle(style);

    }

    protected void initListBoxStyle() {

        Style style = new Style(".", DefaultWidgetsTheme.StyleName.ListBox, "-", StyleDependent.readonly);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.1);
        style.addProperty("background-color", ThemeColor.foreground, 0);
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.ListBox, "-", StyleDependent.disabled);
        style.addProperty("background-color", ThemeColor.foreground, 0.3);
        addStyle(style);

    }

    protected void initTriggerButtonStyle() {

        Style style = new Style(".", StyleName.EditorPanel, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("background", "transparent");
        style.addProperty("border", "none");
        style.addProperty("padding", "0");
        style.addProperty("height", "19px");
        addStyle(style);

        style = new Style(".", StyleName.ViewerPanel, " .", DefaultWidgetsTheme.StyleName.Button);
        style.addProperty("background", "transparent");
        style.addProperty("border", "none");
        style.addProperty("padding", "0");
        style.addProperty("height", "19px");
        addStyle(style);

    }

    protected void initValidationLabelStyle() {
        Style style = new Style(".", StyleName.ValidationLabel);
        style.addProperty("color", "red");
        addStyle(style);
    }

    protected void initNoteStyle() {
        Style style = new Style(".", StyleName.NoteLabel);
        style.addProperty("font-weight", "normal");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".", StyleName.NoteLabel, "-", StyleDependent.info);
        style.addProperty("color", ThemeColor.foreground, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.NoteLabel, "-", StyleDependent.warning);
        style.addProperty("color", ThemeColor.object1, 1);
        addStyle(style);

    }
}
