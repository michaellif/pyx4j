/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Theme {

    private static final Logger log = LoggerFactory.getLogger(Theme.class);

    private final String discriminator;

    private final List<Style> styles;

    public Theme() {
        this(null);
    }

    public Theme(String discriminator) {
        this.discriminator = discriminator;
        styles = new ArrayList<Style>();
    }

    public List<Style> getAllStyles() {
        return styles;
    }

    public List<Style> getStyles(String selector) {
        List<Style> r = new Vector<Style>();
        for (Style style : styles) {
            if (selector.equals(style.getSelector())) {
                r.add(style);
            }
        }
        return Collections.unmodifiableList(r);
    }

    public void addStyle(Style style) {
        if (style == null) {
            throw new Error("Style can't be null");
        }

        styles.add(style);
    }

    public void addTheme(Theme theme) {
        styles.addAll(theme.getAllStyles());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Theme) {
            return this.getClass().equals(((Theme) obj).getClass());
        }
        return false;
    }

    public String getDiscriminator() {
        return discriminator;
    }

}
