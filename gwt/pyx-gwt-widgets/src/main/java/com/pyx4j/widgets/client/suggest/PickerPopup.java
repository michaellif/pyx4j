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
package com.pyx4j.widgets.client.suggest;

import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class PickerPopup<E> extends PopupPanel {

    private final ISelectorWidget<E> selectorWidget;

    private IPickerPanel<E> pickerPanel;

    public PickerPopup(final ISelectorWidget<E> parent) {
        super(true, false);
        this.selectorWidget = parent;
        addAutoHidePartner(parent.getElement());

        setPreviewingAllNativeEvents(true);
        addStyleName(WidgetTheme.StyleName.SuggestBoxPopup.name());

    }

    public ISelectorWidget<E> getSelectorWidget() {
        return selectorWidget;
    }

    public void show(IPickerPanel<E> pickerPanel) {
        if (isAttached()) {
            hide();
        }
        this.pickerPanel = pickerPanel;
        pickerPanel.setPickerPopup(this);
        setWidget(pickerPanel.asWidget());
        showRelativeTo(selectorWidget.asWidget());
    }

    @Override
    public void hide() {
        this.pickerPanel = null;
        if (pickerPanel != null) {
            pickerPanel.setPickerPopup(null);
        }
        setWidget(null);
        super.hide();
    }

    public void moveSelectionDown() {
        if (pickerPanel != null) {
            pickerPanel.moveSelectionDown();
        }
    }

    public void moveSelectionUp() {
        if (pickerPanel != null) {
            pickerPanel.moveSelectionUp();
        }
    }

    public void pickSelection() {
        if (pickerPanel != null) {
            pickerPanel.pickSelection();
        }
    };

    public void refreshSuggestions(String query) {
        if (pickerPanel != null) {
            pickerPanel.refreshOptions(query);
        }
    }

    @Override
    protected void onLoad() {
        setWidth(selectorWidget.asWidget().getOffsetWidth() + "px");
    }

}
