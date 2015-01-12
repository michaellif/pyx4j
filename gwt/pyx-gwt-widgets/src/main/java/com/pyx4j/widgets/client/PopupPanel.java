/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Sep 10, 2012
 * @author michaellif
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;

public class PopupPanel extends com.google.gwt.user.client.ui.PopupPanel {

    // Set of open panels so we can close them on window resize, because resizing
    // the window is equivalent to the user clicking outside the widget.
    private static ArrayList<PopupPanel> openPopups = new ArrayList<>();

    static ArrayList<PopupPanel> getOpenPopups() {
        return openPopups;
    }

    public PopupPanel(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        getElement().getStyle().setZIndex(50);
    }

    @Override
    public void show() {
        if (isShowing()) {
            return;
        }
        openPopups.add(this);
        super.show();
    }

    @Override
    public final void hide() {
        hide(false);
    }

    @Override
    public void hide(boolean autoClosed) {
        if (!isShowing()) {
            return;
        }
        super.hide(autoClosed);
        openPopups.remove(this);
    }

    public static void hideAll() {
        ArrayList<PopupPanel> popups = new ArrayList<>(openPopups);
        for (PopupPanel popupPanel : popups) {
            popupPanel.hide(true);
        }
    }

}