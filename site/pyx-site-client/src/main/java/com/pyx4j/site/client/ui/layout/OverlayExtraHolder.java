/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 13, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout;

import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.ui.devconsole.AbstractDevConsole;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class OverlayExtraHolder extends OverlayHolder {

    private final ResponsiveLayoutPanel parent;

    private final LayoutPanel overlayExtra1Holder;

    private final LayoutPanel overlayExtra2Holder;

    public OverlayExtraHolder(ResponsiveLayoutPanel parent, String extra1Caption, String extra2Caption, AbstractDevConsole devConsole) {
        this.parent = parent;
        if (ApplicationMode.isDevelopment()) {
            addTab(devConsole, "Dev. Console");
        }

        overlayExtra1Holder = new LayoutPanel();
        addTab(overlayExtra1Holder, extra1Caption == null ? "" : extra1Caption);
        setTabVisible(getTabIndex(overlayExtra1Holder), false);

        overlayExtra2Holder = new LayoutPanel();
        addTab(overlayExtra2Holder, extra2Caption == null ? "" : extra2Caption);
        setTabVisible(getTabIndex(overlayExtra2Holder), false);
    }

    public void layout() {
        if (parent.getDisplay(DisplayType.extra1).getWidget() != null) {
            overlayExtra1Holder.clear();
            overlayExtra1Holder.add(parent.getDisplay(DisplayType.extra1));
            setTabVisible(getTabIndex(overlayExtra1Holder), true);
        }
        if (parent.getDisplay(DisplayType.extra2).getWidget() != null) {
            overlayExtra2Holder.clear();
            overlayExtra2Holder.add(parent.getDisplay(DisplayType.extra2));
            setTabVisible(getTabIndex(overlayExtra2Holder), true);
        }
    }

    public void hide() {
        setTabVisible(getTabIndex(overlayExtra1Holder), false);
        setTabVisible(getTabIndex(overlayExtra2Holder), false);
    }

}
