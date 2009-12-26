/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import java.util.List;

public interface Theme {

    public enum ThemeColorProperty {

        OBJECT_TONE1,

        OBJECT_TONE2,

        OBJECT_TONE3,

        BORDER,

        SELECTION,

        SELECTION_TEXT,

        SEPARATOR,

        TEXT,

        TEXT_BACKGROUND,

        DISABLED_TEXT_BACKGROUND,

        MANDATORY_TEXT_BACKGROUND,

        READ_ONLY_TEXT_BACKGROUND
    }

    public enum CSSClass {
        pyx4j_Toolbar,

        pyx4j_StatusBar,

        pyx4j_BarSeparator,

        //Section Panel
        pyx4j_Section, pyx4j_Section_Border, pyx4j_Section_SelectionBorder, pyx4j_Section_Background, pyx4j_Section_Content, pyx4j_Section_ContentBorder, pyx4j_Section_header2Holder,

        //Folder Panel
        pyx4j_Folder,

        //Button
        pyx4j_Button,

        //Tooltip
        pyx4j_Tooltip,

        //Dialog
        pyx4j_Dialog, pyx4j_Dialog_Caption, pyx4j_Dialog_Resizer, pyx4j_Dialog_Content

    }

    public String getProperty(ThemeColorProperty name);

    public List<Style> getStyles();

    public void compileStyles();

    public Style getBodyStyle();

    public List<Style> getSectionStyles();

    public Style getToolbarStyle();

    public Style getStatusBarStyle();

    public Style getBarSeparatorStyle();

    public List<Style> getProgressBarStyles();

    public List<Style> getMenuBarStyles();

    public List<Style> getTabPanelStyles();

    public List<Style> getDialogBoxStyles();

    public List<Style> getDialogPanelStyles();

    public List<Style> getButtonStyles();

    public Style getTooltipStyle();
}
