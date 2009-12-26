/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstarctTheme implements Theme {

    protected final List<Style> styles = new ArrayList<Style>();

    private final HashMap<ThemeColorProperty, String> properties = new HashMap<ThemeColorProperty, String>();

    public AbstarctTheme() {
    }

    @Override
    public String getProperty(ThemeColorProperty name) {
        return properties.get(name);
    }

    public void setProperty(ThemeColorProperty name, String value) {
        properties.put(name, value);
    }

    public void compileStyles() {
        styles.clear();
        styles.add(getBodyStyle());
        styles.addAll(getSectionStyles());
        styles.add(getToolbarStyle());
        styles.add(getBarSeparatorStyle());
        styles.add(getStatusBarStyle());
        styles.addAll(getProgressBarStyles());
        styles.addAll(getMenuBarStyles());
        styles.addAll(getTabPanelStyles());
        styles.addAll(getDialogBoxStyles());
        styles.addAll(getDialogPanelStyles());
        styles.addAll(getButtonStyles());
        styles.add(getTooltipStyle());
    }

    public List<Style> getStyles() {
        return styles;
    }

    public static Style getStyleBySelector(List<Style> list, String selector) {
        for (Style s : list) {
            if (s.getSelector().equals(selector)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public Style getBodyStyle() {
        Style style = new Style("body");
        style.addProperty("background-color", ThemeColorProperty.OBJECT_TONE1);
        style.addProperty("color", ThemeColorProperty.TEXT);
        return style;
    }

    @Override
    public List<Style> getSectionStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style("." + CSSClass.pyx4j_Section_Border.name());
        style.addProperty("background-color", ThemeColorProperty.BORDER);
        styles.add(style);

        style = new Style("." + CSSClass.pyx4j_Section_SelectionBorder.name());
        style.addProperty("background-color", ThemeColorProperty.SELECTION);
        styles.add(style);

        style = new Style("." + CSSClass.pyx4j_Section_Background.name());
        style.addProperty("background-color", ThemeColorProperty.OBJECT_TONE1);
        styles.add(style);

        style = new Style("." + CSSClass.pyx4j_Section_Content.name());
        style.addProperty("background-color", ThemeColorProperty.TEXT_BACKGROUND);
        styles.add(style);

        return styles;
    }

    @Override
    public Style getToolbarStyle() {
        Style style = new Style("." + CSSClass.pyx4j_Toolbar.name());
        style.addProperty("background-color", ThemeColorProperty.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");
        return style;
    }

    @Override
    public Style getStatusBarStyle() {
        Style style = new Style("." + CSSClass.pyx4j_StatusBar.name());
        style.addProperty("background-color", ThemeColorProperty.OBJECT_TONE1);
        style.addProperty("padding", "2 2 2 8");
        return style;
    }

    @Override
    public Style getBarSeparatorStyle() {
        Style style = new Style("." + CSSClass.pyx4j_BarSeparator.name());
        style.addProperty("border-left-width", "2px");
        style.addProperty("border-left-style", "ridge");
        style.addProperty("border-color", ThemeColorProperty.OBJECT_TONE1);

        style.addProperty("height", "15px");
        style.addProperty("margin-left", "3px");
        style.addProperty("margin-top", "2px");
        style.addProperty("margin-bottom", "2px");
        return style;
    }

    @Override
    public List<Style> getProgressBarStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style(".gwt-ProgressBar-shell");
        style.addProperty("background-color", ThemeColorProperty.BORDER);
        styles.add(style);

        style = new Style(".gwt-ProgressBar-bar");
        style.addProperty("background-color", ThemeColorProperty.SELECTION);
        styles.add(style);

        style = new Style(".gwt-ProgressBar-text");
        style.addProperty("color", ThemeColorProperty.SELECTION_TEXT);
        styles.add(style);

        return styles;
    }

    @Override
    public List<Style> getMenuBarStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style(".gwt-MenuBar");
        style.addProperty("cursor", "default");
        styles.add(style);

        style = new Style(".gwt-MenuBar .gwt-MenuItem");
        style.addProperty("cursor", "default");
        styles.add(style);

        style = new Style(".gwt-MenuBar .gwt-MenuItem-selected");
        style.addProperty("background", ThemeColorProperty.SELECTION);
        style.addProperty("color", ThemeColorProperty.SELECTION_TEXT);
        styles.add(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("margin-top", "0px");
        style.addProperty("margin-left", "0px");
        style.addProperty("background", ThemeColorProperty.TEXT_BACKGROUND);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColorProperty.SELECTION);
        styles.add(style);

        style = new Style(".gwt-MenuBar-vertical .gwt-MenuItem");
        styles.add(style);
        style.addProperty("padding", "4px 14px 4px 1px");

        style = new Style(".gwt-MenuBar-horizontal");
        styles.add(style);

        style = new Style(".gwt-MenuBar-horizontal .gwt-MenuItem");
        style.addProperty("padding", "0px 10px");
        style.addProperty("vertical-align", "bottom");
        styles.add(style);

        return styles;
    }

    @Override
    public List<Style> getDialogBoxStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style(".gwt-DialogBox");
        styles.add(style);
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "outset");
        style.addProperty("border-color", ThemeColorProperty.BORDER);
        style.addProperty("background-color", ThemeColorProperty.OBJECT_TONE3);

        style = new Style(".gwt-DialogBox .Caption");
        styles.add(style);
        style.addProperty("background-color", ThemeColorProperty.SELECTION);

        return styles;
    }

    @Override
    public List<Style> getDialogPanelStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style("." + CSSClass.pyx4j_Dialog.name());
        styles.add(style);
        style.addProperty("border-color", ThemeColorProperty.SELECTION);

        style = new Style("." + CSSClass.pyx4j_Dialog_Caption.name());
        styles.add(style);
        style.addProperty("background", ThemeColorProperty.SELECTION);
        style.addProperty("filter", "alpha(opacity=80)");
        style.addProperty("opacity", "0.8");
        style.addProperty("color", ThemeColorProperty.SELECTION_TEXT);
        style.addProperty("font-weight", "bold");

        style = new Style("." + CSSClass.pyx4j_Dialog_Resizer.name());
        styles.add(style);
        style.addProperty("background", ThemeColorProperty.SELECTION);
        style.addProperty("filter", "alpha(opacity=80)");
        style.addProperty("opacity", "0.8");

        style = new Style("." + CSSClass.pyx4j_Dialog_Content.name());
        styles.add(style);
        style.addProperty("background-color", ThemeColorProperty.TEXT_BACKGROUND);

        return styles;
    }

    @Override
    public List<Style> getTabPanelStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style(".gwt-TabPanel");
        styles.add(style);

        style = new Style(".gwt-TabPanelBottom");
        style.addProperty("padding", "2px");
        style.addProperty("margin", "0px");
        styles.add(style);

        style = new Style(".gwt-TabBarMoveLeft");
        style.addProperty("margin", "3px");
        styles.add(style);

        style = new Style(".gwt-TabBarMoveRight");
        style.addProperty("margin", "3px");
        styles.add(style);

        style = new Style(".gwt-TabBarItem");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-align", "center");
        style.addProperty("border-right-width", "1px");
        style.addProperty("border-right-style", "solid");
        style.addProperty("border-right-color", ThemeColorProperty.BORDER);
        style.addProperty("background-color", ThemeColorProperty.OBJECT_TONE3);
        styles.add(style);

        style = new Style(".gwt-TabBarItem-first");
        style.addProperty("border-left-width", "1px");
        style.addProperty("border-left-style", "solid");
        style.addProperty("border-left-color", ThemeColorProperty.BORDER);
        styles.add(style);

        style = new Style(".gwt-TabBarItem-selected");
        style.addProperty("cursor", "default");
        style.addProperty("background-color", ThemeColorProperty.SELECTION);
        styles.add(style);

        style = new Style(".gwt-TabBarItem-selected .gwt-TabBarItemLabel");
        style.addProperty("color", ThemeColorProperty.SELECTION_TEXT);
        styles.add(style);

        return styles;

    }

    @Override
    public List<Style> getButtonStyles() {
        List<Style> styles = new ArrayList<Style>();

        Style style = new Style("." + CSSClass.pyx4j_Button.name());
        style.addProperty("padding", "3px");
        style.addProperty("margin", "1px");
        style.addProperty("border", "2px solid transparent");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("outline", "none");
        styles.add(style);

        style = new Style("." + CSSClass.pyx4j_Button.name() + "-hover");
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "outset");
        style.addProperty("border-color", ThemeColorProperty.OBJECT_TONE3);
        styles.add(style);

        style = new Style("." + CSSClass.pyx4j_Button.name() + "-pushed");
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "ridge");
        style.addProperty("border-color", ThemeColorProperty.OBJECT_TONE3);
        styles.add(style);

        style = new Style("." + CSSClass.pyx4j_Button.name() + "-checked");
        style.addProperty("background", ThemeColorProperty.OBJECT_TONE2);
        style.addProperty("border-width", "2px");
        style.addProperty("border-style", "inset");
        style.addProperty("border-color", ThemeColorProperty.OBJECT_TONE3);
        styles.add(style);

        return styles;

    }

    @Override
    public Style getTooltipStyle() {
        Style style = new Style("." + CSSClass.pyx4j_Tooltip.name());
        style.addProperty("border", "1px solid #000000");
        style.addProperty("background-color", "#FFFFCC");
        style.addProperty("padding", "1px 3px 1px 3px");
        style.addProperty("color", "#000000");
        style.addProperty("font-size", "16px");
        return style;
    }

}
