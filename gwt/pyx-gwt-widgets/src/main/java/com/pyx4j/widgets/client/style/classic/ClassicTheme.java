/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 18, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client.style.classic;

import java.util.List;

import com.pyx4j.widgets.client.style.AbstarctTheme;
import com.pyx4j.widgets.client.style.Style;

public class ClassicTheme extends AbstarctTheme {

    public ClassicTheme() {
        setProperty(ThemeColorProperty.OBJECT_TONE1, "#FFFFFF"); //
        setProperty(ThemeColorProperty.OBJECT_TONE2, "#fdfae9");
        setProperty(ThemeColorProperty.OBJECT_TONE3, "#FFFFFF"); //
        setProperty(ThemeColorProperty.BORDER, "#7AA5D6"); //
        setProperty(ThemeColorProperty.SELECTION, "#7AA5D6"); //
        setProperty(ThemeColorProperty.SELECTION_TEXT, "#224499"); //
        setProperty(ThemeColorProperty.TEXT, "#000000");
        setProperty(ThemeColorProperty.TEXT_BACKGROUND, "#ffffff");
        setProperty(ThemeColorProperty.DISABLED_TEXT_BACKGROUND, "#fafafa");
        setProperty(ThemeColorProperty.MANDATORY_TEXT_BACKGROUND, "#fcba84");
        setProperty(ThemeColorProperty.READ_ONLY_TEXT_BACKGROUND, "#eeeeee");
        setProperty(ThemeColorProperty.SEPARATOR, "#eeeeee");
    }

    @Override
    public List<Style> getMenuBarStyles() {
        List<Style> styles = super.getMenuBarStyles();

        Style style = getStyleBySelector(styles, ".gwt-MenuBar .gwt-MenuItem-selected");
        style.removeProperty("color");

        style = getStyleBySelector(styles, ".gwt-MenuBar-horizontal");
        style.addProperty("background", "#e3e8f3 url(images/hborder.png) repeat-x 0px -2003px");
        style.addProperty("border", "1px solid " + getProperty(ThemeColorProperty.SEPARATOR));

        style = getStyleBySelector(styles, ".gwt-MenuBar-vertical");
        style.removeProperty("border-width");
        style.removeProperty("border-style");
        style.removeProperty("border-color");

        style = new Style(".gwt-MenuBarPopup");
        styles.add(style);
        style.addProperties("margin: 0px 0px 0px 3px");

        style = new Style(".gwt-MenuBarPopup .menuPopupTopCenter");
        styles.add(style);
        style.addProperties("background: url(images/hborder.png) 0px -12px repeat-x");

        style = new Style(".gwt-MenuBarPopup .menuPopupBottomCenter");
        styles.add(style);
        style.addProperties("background: url(images/hborder.png) 0px -13px repeat-x");

        style = new Style(".gwt-MenuBarPopup .menuPopupMiddleLeft");
        styles.add(style);
        style.addProperties("background: url(images/vborder.png) -12px 0px repeat-y");

        style = new Style(".gwt-MenuBarPopup .menuPopupMiddleRight");
        styles.add(style);
        style.addProperties("background: url(images/vborder.png) -13px 0px repeat-y");

        style = new Style(".gwt-MenuBarPopup .menuPopupTopLeftInner");
        styles.add(style);
        style.addProperties("width: 5px; height: 5px; zoom: 1;");

        style = new Style(".gwt-MenuBarPopup .menuPopupTopRightInner");
        styles.add(style);
        style.addProperties("width: 8px; height: 5px; zoom: 1;");

        style = new Style(".gwt-MenuBarPopup .menuPopupBottomLeftInner");
        styles.add(style);
        style.addProperties("width: 5px; height: 8px; zoom: 1;");

        style = new Style(".gwt-MenuBarPopup .menuPopupBottomRightInner");
        styles.add(style);
        style.addProperties("width: 8px; height: 8px; zoom: 1;");

        style = new Style(".gwt-MenuBarPopup .menuPopupTopLeft");
        styles.add(style);
        style.addProperties("background: url(images/corner.png) no-repeat 0px -36px;");

        style = new Style(".gwt-MenuBarPopup .menuPopupTopRight");
        styles.add(style);
        style.addProperties("background: url(images/corner.png) no-repeat -5px -36px;");

        style = new Style(".gwt-MenuBarPopup .menuPopupBottomLeft");
        styles.add(style);
        style.addProperties("background: url(images/corner.png) no-repeat 0px -41px;");

        style = new Style(".gwt-MenuBarPopup .menuPopupBottomRight");
        styles.add(style);
        style.addProperties("background: url(images/corner.png) no-repeat -5px -41px;");

        style = new Style("* html .gwt-MenuBarPopup .menuPopupTopLeftInner");
        styles.add(style);
        style.addProperties("width: 5px;height: 5px;overflow: hidden;");

        style = new Style("* html .gwt-MenuBarPopup .menuPopupTopRightInner");
        styles.add(style);
        style.addProperties("width: 8px;height: 5px;overflow: hidden;");

        style = new Style("* html .gwt-MenuBarPopup .menuPopupBottomLeftInner");
        styles.add(style);
        style.addProperties("width: 5px;height: 8px;overflow: hidden;");

        style = new Style("* html .gwt-MenuBarPopup .menuPopupBottomRightInner");
        styles.add(style);
        style.addProperties("width: 8px;height: 8px;overflow: hidden");

        return styles;
    }

    @Override
    public List<Style> getButtonStyles() {
        List<Style> styles = super.getButtonStyles();

        Style style = new Style(".gwt-Button");
        styles.add(style);
        style.addProperties("margin: 0; padding: 3px 5px;");
        style.addProperties("text-decoration: none;font-family : Verdana, Arial, Helvetica, sans-serif;font-size : 11px;");
        style.addProperties("cursor: pointer;cursor: hand;");
        style.addProperties("background: url(images/hborder.png) repeat-x 0px -27px;");
        style.addProperties("border: 1px outset #ccc");

        style = new Style(".gwt-Button:active");
        styles.add(style);
        style.addProperties("border: 1px inset #ccc");

        style = new Style(".gwt-Button:hover");
        styles.add(style);
        style.addProperties("border-color: #9cf #69e #69e #7af");

        style = new Style(".gwt-Button[disabled]");
        styles.add(style);
        style.addProperties("cursor: default;color: #888;");

        style = new Style(".gwt-Button[disabled]:hover");
        styles.add(style);
        style.addProperties("border: 1px outset #ccc");

        return styles;
    }

}
