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
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.ui.HasStyle;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class DropDownPanel extends PopupPanel implements HasStyle {

    static {
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                if (PopupPanel.getOpenPopups() != null) {
                    for (PopupPanel panel : PopupPanel.getOpenPopups()) {
                        assert (panel.isShowing());
                        if (panel instanceof DropDownPanel) {
                            ((DropDownPanel) panel).redraw();
                        }
                    }
                }
            }
        });
    }

    private static ArrayList<PopupPanel> openDropDowns = new ArrayList<>();

    private Widget anchor;

    private PositionCallback positionCallback;

    /**
     * Creates a new drop down panel.
     */
    public DropDownPanel() {
        super(true, false);
        setStyleName(WidgetsTheme.StyleName.DropDownPanel.name());
        setPreviewingAllNativeEvents(true);
    }

    public void showRelativeTo(Widget anchor) {
        showRelativeTo(anchor, null);
    }

    public void showRelativeTo(final Widget anchor, final PositionCallback positionCallback) {
        if (this.anchor != anchor) {
            if (this.anchor != null) {
                this.removeAutoHidePartner(this.anchor.getElement());
            }
            if (anchor != null) {
                this.addAutoHidePartner(anchor.getElement());
            }
            this.anchor = anchor;
        }
        this.positionCallback = positionCallback;
        redraw();
    }

    private void redraw() {
        if (positionCallback == null) {
            super.showRelativeTo(anchor);
        } else {
            setPopupPositionAndShow(positionCallback);
        }
    }

    @Override
    public void show() {
        if (isShowing()) {
            return;
        }
        if (openDropDowns.size() > 0) {
            openDropDowns.get(openDropDowns.size() - 1).setAutoHideEnabled(false);
        }
        setAutoHideEnabled(true);
        openDropDowns.add(this);
        super.show();
    }

    @Override
    public void hide(boolean autoClosed) {
        if (!isShowing()) {
            return;
        }
        super.hide(autoClosed);
        openDropDowns.remove(this);
        if (openDropDowns.size() > 0) {
            openDropDowns.get(openDropDowns.size() - 1).setAutoHideEnabled(true);
        }
    }
}
