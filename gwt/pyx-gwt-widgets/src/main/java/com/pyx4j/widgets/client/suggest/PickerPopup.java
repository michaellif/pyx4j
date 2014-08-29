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

    private final AbstractSelectorWidget<E> parent;

    public PickerPopup(AbstractSelectorWidget<E> parent) {
        super(true, false);
        this.parent = parent;
        addAutoHidePartner(parent.getElement());
        setPreviewingAllNativeEvents(true);
        setStyleName(WidgetTheme.StyleName.SelectionBoxPicker.name());
    }

    public void show(IPickerPanel<E> pickerPanel) {
        if (isAttached()) {
            hide();
        }

        pickerPanel.setSelectorWidget(parent);
        setWidget(pickerPanel.asWidget());
        showRelativeTo(parent);
    }

    @Override
    protected void onLoad() {
        if (getOffsetWidth() <= parent.getOffsetWidth()) {
            setWidth((parent.getOffsetWidth() - 2) + "px");
        } else {
            setWidth((getOffsetWidth() + 50) + "px");
        }
    };

}
