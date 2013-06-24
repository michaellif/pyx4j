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
package com.pyx4j.widgets.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class DropDownPanel extends PopupPanel {

    private Widget currentAnchor;

    /**
     * Creates a new drop down panel.
     */
    public DropDownPanel() {
        super(true, false);
        setStyleName(DefaultWidgetsTheme.StyleName.DropDownPanel.name());
        setPreviewingAllNativeEvents(true);
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                if (PopupPanel.getOpenPopups() != null) {
                    for (PopupPanel panel : PopupPanel.getOpenPopups()) {
                        assert (panel.isShowing());

                        if (panel instanceof DropDownPanel && ((DropDownPanel) panel).currentAnchor != null) {
                            panel.showRelativeTo(((DropDownPanel) panel).currentAnchor);
                        }
                    }
                }
            }
        });
    }

    public void showRelativeTo(Widget anchor) {
        if (currentAnchor != null) {
            this.removeAutoHidePartner(currentAnchor.getElement());
        }
        if (anchor != null) {
            this.addAutoHidePartner(anchor.getElement());
        }
        currentAnchor = anchor;
        super.showRelativeTo(anchor);
    }

}
