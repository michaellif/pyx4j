package com.pyx4j.widgets.client.richtext;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class RichTextEditorTheme extends Theme {

    public static enum StyleName implements IStyleName {
        ReachTextEditor, ReachTextArea, RteToolbar, RteToolbarButton, RteToolbarButtonNoToggle, RteCheckBox
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
        Style style = new Style(".", StyleName.ReachTextEditor, " .", WidgetTheme.StyleName.Button, ".", StyleName.RteToolbarButton);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("height", "1.5em");
        style.addProperty("line-height", "1.2em");
        style.addProperty("padding", "2px 3px");
        style.addProperty("margin", "2px");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetTheme.StyleName.Button, ".", StyleName.RteToolbarButton);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.formBackground);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetTheme.StyleName.Button, ".", StyleName.RteToolbarButton, " .",
                WidgetTheme.StyleName.ButtonText);
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", StyleName.RteToolbarButton, ".", WidgetTheme.StyleName.Button, "-",
                WidgetTheme.StyleDependent.active);
        style.addProperty("border-color", ThemeColor.foreground, 0.3);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetTheme.StyleName.Button, ".", StyleName.RteToolbarButtonNoToggle);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("height", "1.5em");
        style.addProperty("line-height", "1.2em");
        style.addProperty("padding", "2px 3px");
        style.addProperty("margin", "2px");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetTheme.StyleName.Button, ".", StyleName.RteToolbarButtonNoToggle);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.formBackground);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetTheme.StyleName.Button, ".", StyleName.RteToolbarButtonNoToggle, " .",
                WidgetTheme.StyleName.ButtonText);
        style.addProperty("line-height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", StyleName.RteToolbarButtonNoToggle, ".", WidgetTheme.StyleName.Button, "-",
                WidgetTheme.StyleDependent.active);
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.formBackground);
        addStyle(style);

        style = new Style(".", StyleName.ReachTextEditor, " .", WidgetTheme.StyleName.CheckBox, ".", StyleName.RteCheckBox);
        style.addProperty("padding", "5px 1px");
        style.addProperty("float", "right");
        addStyle(style);
    }
}
