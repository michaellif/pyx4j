/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 17, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.ria.client.style;

import java.util.EnumSet;
import java.util.List;

import com.pyx4j.widgets.client.style.AbstarctTheme;
import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.Theme;

public class EditableTheme extends AbstarctTheme {

    public EditableTheme() {
    }

    @Override
    public void compileStyles() {

    }

    public void importTheme(Theme theme) {
        for (ThemeColorProperty p : EnumSet.allOf(ThemeColorProperty.class)) {
            setProperty(p, theme.getProperty(p));
        }
        List<Style> styles = theme.getStyles();
        if (styles != this.styles) {
            this.styles.clear();
            this.styles.addAll(styles);
        }
    }
}
