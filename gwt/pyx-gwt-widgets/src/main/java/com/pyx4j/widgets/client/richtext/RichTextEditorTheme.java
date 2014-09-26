package com.pyx4j.widgets.client.richtext;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;

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
        Style style = new Style(".", StyleName.rteTopBarButton);
        style.addProperty("color", ThemeColor.foreground);
        addStyle(style);

    }
}
