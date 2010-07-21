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
 * Created on Jul 20, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.combobox;

import java.util.ArrayList;
import java.util.List;

public class ListBox<E> extends ComboBox<E> {

    private final List<E> options = new ArrayList<E>();

    public ListBox(boolean multipleSelect) {

        setTextBox(new com.google.gwt.user.client.ui.TextBox());
        setPickerPanel(new ListPickerPanel<E>(this, multipleSelect));
        setOptionsGrabber(new ListOptionsGrabber<E>(this));

        init();
    }

    public List<E> getOptions() {
        return options;
    }

    public void setOptions(List<E> options) {
        this.options.clear();
        this.options.addAll(options);
    }

}
