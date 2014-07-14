package com.pyx4j.widgets.client.richtext;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

public class RichTextEditorTheme extends Theme {

    public static enum StyleName implements IStyleName {
        rtePushButton, rteToggleButton
    }

    public RichTextEditorTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        // Toggle Button
        Style style = new Style(".", StyleName.rteToggleButton);
        style.addProperty("color", ThemeColor.background);
        style.addProperty("border-width", "1px");
        style.addProperty("padding", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.background);
        style.addProperty("margin", "3px");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.rteToggleButton, "-up");
        addStyle(style);

        style = new Style(".", StyleName.rteToggleButton, "-up-hovering");
        style.addProperty("border-color", ThemeColor.foreground);
        addStyle(style);

        style = new Style(".", StyleName.rteToggleButton, "-down");
        style.addProperty("border-width", "2px");
        style.addProperty("margin", "2px");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("border-style", "inset");
        addStyle(style);

        style = new Style(".", StyleName.rteToggleButton, "-down-hovering");
        style.addProperty("border-width", "2px");
        style.addProperty("margin", "2px");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("border-style", "inset");
        addStyle(style);

        // Push Button
        style = new Style(".", StyleName.rtePushButton);
        style.addProperty("color", ThemeColor.background);
        style.addProperty("border-width", "1px");
        style.addProperty("border-style", "solid");
        style.addProperty("border-color", ThemeColor.background);
        style.addProperty("margin", "0.2em 0.2em");
        style.addProperty("padding", "0.2em 0.5em");
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", StyleName.rtePushButton, "-up-hovering");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.rtePushButton, "-down");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.rtePushButton, "-down-hovering");
        style.addProperty("border-color", ThemeColor.foreground);
        style.addProperty("cursor", "pointer");
        addStyle(style);
    }
}
