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
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class CComponentTheme extends Theme {

    public static enum StyleName implements IStyleName {
        ComponentHolder, FieldEditorPanel, FieldViewerPanel, ValidationLabel, NoteLabel, Signature,

        TabbedFormTab,

        ImageEditorMenu
    }

    public static enum StyleDependent implements IStyleDependent {
        info, warning
    }

    public CComponentTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        initGeneralStyle();
        initTriggerButtonStyle();
        initValidationLabelStyle();
        initNoteStyle();
        initTabbedFormStyle();
        initImageHolderStyle();
        initEditorPanelStyle();
        initViewerPanelStyle();
    }

    private void initGeneralStyle() {
        Style style = new Style(".", StyleName.ComponentHolder);
        addStyle(style);
    }

    private void initEditorPanelStyle() {
        Style style = new Style(".", StyleName.FieldEditorPanel, " .", StyleName.Signature);
        style.addProperty("line-height", "20px");
        style.addProperty("width", "100%");
        addStyle(style);
    }

    private void initViewerPanelStyle() {
        Style style = new Style(".", StyleName.FieldViewerPanel, " .", WidgetsTheme.StyleName.Label);
        style.addProperty("white-space", "normal");
        addStyle(style);
    }

    protected void initTriggerButtonStyle() {

        Style style = new Style(".", StyleName.FieldEditorPanel, " .", WidgetsTheme.StyleName.Button);
        style.addProperty("background", "transparent");
        style.addProperty("border", "none");
        style.addProperty("padding", "0 2px");
        style.addProperty("height", "19px");
        style.addProperty("display", "inline-block");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StyleName.FieldViewerPanel, " .", WidgetsTheme.StyleName.Button);
        style.addProperty("background", "transparent");
        style.addProperty("border", "none");
        style.addProperty("padding", "0");
        style.addProperty("height", "19px");
        addStyle(style);

    }

    protected void initValidationLabelStyle() {
        Style style = new Style(".", StyleName.ValidationLabel);
        style.addProperty("color", "red");
        style.addProperty("text-align", "left");
        style.addProperty("white-space", "normal");
        addStyle(style);
    }

    protected void initNoteStyle() {
        Style style = new Style(".", StyleName.NoteLabel);
        style.addProperty("font-weight", "normal");
        style.addProperty("font-style", "italic");
        style.addProperty("text-align", "left");
        style.addProperty("white-space", "normal");
        style.addProperty("padding-top", "3px");
        addStyle(style);

        style = new Style(".", StyleName.NoteLabel, "-", StyleDependent.info);
        style.addProperty("color", ThemeColor.foreground, 0.7);
        addStyle(style);

        style = new Style(".", StyleName.NoteLabel, "-", StyleDependent.warning);
        style.addProperty("color", "orange");
        addStyle(style);

    }

    protected void initTabbedFormStyle() {
        Style style = new Style(".", StyleName.TabbedFormTab);
        style.addProperty("margin", "6px");
        //Keep empty space on a bottom of page for bottom tab 
        style.addProperty("padding-bottom", "40px");
        addStyle(style);
    }

    private void initImageHolderStyle() {
        Style style = new Style(".", StyleName.ImageEditorMenu, " .", WidgetsTheme.StyleName.Button);
        style.addProperty("background", ThemeColor.foreground, 0.1);
        style.addProperty("border-radius", "0px");
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        style.addProperty("padding", "0");
        style.addProperty("margin", "0");
        style.addProperty("display", "block");
        style.addProperty("color", ThemeColor.foreground, 0.7);
        style.addProperty("cursor", "pointer");
        addStyle(style);
    }
}
