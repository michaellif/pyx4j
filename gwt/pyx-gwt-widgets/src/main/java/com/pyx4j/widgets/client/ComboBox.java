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
 * Created on Jul 15, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBoxBase;

import com.pyx4j.widgets.client.style.CSSClass;

public class ComboBox extends HorizontalPanel {

    private final TextBoxBase box;

    public ComboBox() {
        this(new com.google.gwt.user.client.ui.TextBox());
    }

    protected ComboBox(TextBoxBase box) {
        box.getElement().getStyle().setBorderStyle(BorderStyle.NONE);

        setStyleName(CSSClass.pyx4j_TextBox.name());

        this.box = box;
        add(box);
        setCellVerticalAlignment(box, ALIGN_MIDDLE);

        Button picker = new Button("", CSSClass.pyx4j_Picker.name());

        add(picker);
        setCellVerticalAlignment(picker, ALIGN_MIDDLE);

    }

}
