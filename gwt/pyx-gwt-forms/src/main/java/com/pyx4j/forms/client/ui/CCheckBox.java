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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.i18n.shared.I18n;

public class CCheckBox extends CFocusComponent<Boolean, NCheckBox> {

    private static final I18n i18n = I18n.get(CRadioGroupBoolean.class);

    private IFormat<Boolean> format;

    private Alignment alignment;

    public enum Alignment {
        left, center, right
    }

    public CCheckBox() {
        this(null);
    }

    public CCheckBox(String title) {
        super(title);
        alignment = Alignment.left;
        populate(false);
        setFormat(new IFormat<Boolean>() {
            @Override
            public String format(Boolean value) {
                if (value == null || !value) {
                    return i18n.tr("No");
                } else {
                    return i18n.tr("Yes");
                }
            }

            @Override
            public Boolean parse(String string) {
                return null;
            }
        });

        setNativeWidget(new NCheckBox(this));
    }

    public IFormat<Boolean> getFormat() {
        return format;
    }

    public void setFormat(IFormat<Boolean> format) {
        this.format = format;
    }

    public void setAlignmet(Alignment alignment) {
        this.alignment = alignment;
        getWidget().setAlignmet(alignment);
    }

    public Alignment getAlignmet() {
        return alignment;
    }

}
