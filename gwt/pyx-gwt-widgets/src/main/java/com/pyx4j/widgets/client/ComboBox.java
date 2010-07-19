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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.CSSClass;
import com.pyx4j.widgets.client.util.BrowserType;
import com.pyx4j.widgets.client.util.BrowserType.Browser;

public class ComboBox extends HorizontalPanel {

    private final TextBoxBase box;

    private final PopupPanel pickerPopup;

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
        picker.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.ui.ListBox list = new com.google.gwt.user.client.ui.ListBox(true);
                list.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
                list.setVisibleItemCount(6);

                list.addItem("Item 1");
                list.addItem("Item 2");
                list.addItem("Item 3");
                list.addItem("Item 4");
                list.addItem("Item 1");
                list.addItem("Item 2");
                list.addItem("Item 3");
                list.addItem("Item 4");
                list.addItem("Item 1");
                list.addItem("Item 2");
                list.addItem("Item 3");
                list.addItem("Item 4");
                list.addItem("Item 1");
                list.addItem("Item 2");
                list.addItem("Item 3");
                list.addItem("Item 4");
                showPicker(list);
            }
        });

        add(picker);
        setCellVerticalAlignment(picker, ALIGN_MIDDLE);

        pickerPopup = createPopup();

    }

    private PopupPanel createPopup() {
        PopupPanel p = new PopupPanel(true, false);
        p.setStyleName("gwt-SuggestBoxPopup");
        p.setPreviewingAllNativeEvents(true);
        p.addAutoHidePartner(getElement());
        return p;
    }

    private void showPicker(Widget picker) {
        if (pickerPopup.isAttached()) {
            pickerPopup.hide();
        }

        pickerPopup.setWidget(picker);

        pickerPopup.showRelativeTo(this);

        //        pickerPopup.setWidth((getOffsetWidth() - 2) + "px");
        //
        //        if (!BrowserType.isIE() && (pickerPopup.getOffsetWidth() > picker.getOffsetWidth())) {
        //            picker.setWidth("100%");
        //        }

        pickerPopup.getElement().getStyle().setBackgroundColor("#fff");
        pickerPopup.setStyleName(CSSClass.pyx4j_TextBox.name());

    }
}
