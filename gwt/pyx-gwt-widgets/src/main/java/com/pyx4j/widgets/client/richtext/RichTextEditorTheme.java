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
        rteTopBarButton
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
        Style style = new Style(".", WidgetTheme.StyleName.Button, ".", StyleName.rteTopBarButton);
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("height", "auto");
        addStyle(style);

    }
}
