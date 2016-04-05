package com.pyx4j.widgets.client.richtext;

import com.pyx4j.commons.css.AtRule;
import com.pyx4j.commons.css.AtRule.AtKeyword;
import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class RichTextTheme extends Theme {

    public static enum StyleName implements IStyleName {
        ReachTextEditor, ReachTextArea, RteToolbar, RteToolbarTop, RteToolbarBottom, RteToolbarButton, RteToolbarButtonNoToggle, RteCheckBox,

        // This defined in RichTextThemeDisplay
        ReachTextDisplay,

        ReachTextViewer;

    }

    public RichTextTheme() {
        initStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    protected void initStyles() {
        // Toggle Button
        Style style = new Style(".", StyleName.ReachTextEditor, " .", WidgetsTheme.StyleName.Button, ".", StyleName.RteToolbarButton);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("height", "1.5em");
        style.addProperty("line-height", "1.2em");
        style.addProperty("padding", "2px 3px");
        style.addProperty("margin", "2px");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetsTheme.StyleName.Button, ".", StyleName.RteToolbarButton);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.formBackground);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetsTheme.StyleName.Button, ".", StyleName.RteToolbarButton, " .",
                WidgetsTheme.StyleName.ButtonText);
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", StyleName.RteToolbarButton, ".", WidgetsTheme.StyleName.Button, "-",
                WidgetsTheme.StyleDependent.active);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetsTheme.StyleName.Button, ".", StyleName.RteToolbarButtonNoToggle);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("height", "1.5em");
        style.addProperty("line-height", "1.2em");
        style.addProperty("padding", "2px 3px");
        style.addProperty("margin", "2px");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetsTheme.StyleName.Button, ".", StyleName.RteToolbarButtonNoToggle);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.formBackground);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetsTheme.StyleName.Button, ".", StyleName.RteToolbarButtonNoToggle, " .",
                WidgetsTheme.StyleName.ButtonText);
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", StyleName.RteToolbarButtonNoToggle, ".", WidgetsTheme.StyleName.Button, "-",
                WidgetsTheme.StyleDependent.active);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.formBackground);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetsTheme.StyleName.CheckBox, ".", StyleName.RteCheckBox);
        style.addProperty("padding", "5px 1px");
        style.addProperty("float", "right");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextViewer);
        style.addProperty("overflow", "auto");
        style.addProperty("height", "100%");
        style.addProperty("max-height", "15em");
        style.addProperty("background", ThemeColor.foreground, 0.02);
        style.addProperty("padding", "2px");
        addStyle(style);

        style = new Style(new AtRule(AtKeyword.media, "print"), ".", StyleName.ReachTextViewer);
        style.addProperty("max-height", "none");
        style.addProperty("height", "auto");
        addStyle(style);

        style = new Style(".", StyleName.RteToolbarTop);
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.RteToolbarBottom);
        style.addProperty("text-align", "left");
        addStyle(style);

    }
}
