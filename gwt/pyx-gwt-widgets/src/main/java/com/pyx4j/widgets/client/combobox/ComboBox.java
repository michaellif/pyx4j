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
package com.pyx4j.widgets.client.combobox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBoxBase;

import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.combobox.OptionsGrabber.Callback;
import com.pyx4j.widgets.client.combobox.OptionsGrabber.Request;
import com.pyx4j.widgets.client.combobox.OptionsGrabber.Response;
import com.pyx4j.widgets.client.style.CSSClass;

public abstract class ComboBox<E> extends HorizontalPanel {

    private final Set<E> selection = new HashSet<E>();

    private PickerPopup<E> pickerPopup;

    private PickerPanel<E> pickerPanel;

    private TextBoxBase textBox;

    private OptionsGrabber<E> optionsGrabber;

    private final boolean multipleSelect;

    protected ComboBox(boolean multipleSelect) {
        this.multipleSelect = multipleSelect;
    }

    protected void init() {

        assert textBox != null;
        assert pickerPanel != null;
        assert optionsGrabber != null;

        textBox.getElement().getStyle().setBorderStyle(BorderStyle.NONE);

        setStyleName(CSSClass.pyx4j_TextBox.name());

        pickerPopup = new PickerPopup<E>(this);

        add(textBox);
        setCellVerticalAlignment(textBox, ALIGN_MIDDLE);

        Button picker = new Button("", CSSClass.pyx4j_Picker.name());
        picker.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                if (!pickerPopup.isShowing()) {
                    if (pickerPanel.requiresOptionsRefresh()) {
                        optionsGrabber.obtainOptions(new Request(), new Callback<E>() {
                            @Override
                            public void onOptionsReady(Request request, Response<E> response) {
                                pickerPanel.setOptions(new ArrayList<E>(response.getOptions()));
                                pickerPanel.setSelection(getSelection());
                                pickerPopup.show(pickerPanel, ComboBox.this);
                            }
                        });
                    } else {
                        pickerPanel.setSelection(getSelection());
                        pickerPopup.show(pickerPanel, ComboBox.this);
                    }
                } else {
                    pickerPopup.hide();
                }

            }
        });

        add(picker);
        setCellVerticalAlignment(picker, ALIGN_MIDDLE);

    }

    protected PickerPopup<E> getPickerPopup() {
        return pickerPopup;
    }

    protected void setPickerPopup(PickerPopup<E> pickerPopup) {
        this.pickerPopup = pickerPopup;
    }

    protected PickerPanel<E> getPickerPanel() {
        return pickerPanel;
    }

    protected void setPickerPanel(PickerPanel<E> pickerPanel) {
        this.pickerPanel = pickerPanel;
    }

    protected TextBoxBase getTextBox() {
        return textBox;
    }

    protected void setTextBox(TextBoxBase textBox) {
        this.textBox = textBox;
    }

    protected void setOptionsGrabber(OptionsGrabber<E> optionsGrabber) {
        this.optionsGrabber = optionsGrabber;
    }

    public OptionsGrabber<E> getOptionsGrabber() {
        return optionsGrabber;
    }

    public void setSelection(E... items) {
        selection.addAll(Arrays.asList(items));
    }

    public Set<E> getSelection() {
        return selection;
    }

}
