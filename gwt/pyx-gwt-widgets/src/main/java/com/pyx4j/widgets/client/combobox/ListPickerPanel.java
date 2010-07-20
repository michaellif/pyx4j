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

import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;

public class ListPickerPanel<E> extends PickerPanel<E> {

    private final OptionsGrabber<E> optionsGrabber;

    private final ListBox<E> listBox;

    private final com.google.gwt.user.client.ui.ListBox list;

    public ListPickerPanel(ListBox<E> listBox, boolean multipleSelect) {

        this.listBox = listBox;

        list = new com.google.gwt.user.client.ui.ListBox(multipleSelect);
        setWidget(list);
        list.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        list.setVisibleItemCount(6);
        optionsGrabber = new ListOptionsGrabber<E>(listBox);
        setOptionsGrabber(optionsGrabber);
    }

    @Override
    protected void setOptions(List<E> options) {
        for (E option : options) {
            list.addItem(option.toString());
        }
    }

}
