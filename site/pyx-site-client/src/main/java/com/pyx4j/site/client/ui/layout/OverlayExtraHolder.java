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
 */
package com.pyx4j.site.client.ui.layout;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.ui.devconsole.AbstractDevConsole;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class OverlayExtraHolder extends AbstractOverlayHolder {

    private final ResponsiveLayoutPanel parent;

    private final LayoutPanel overlayDevConsoleHolder;

    private final LayoutPanel overlayExtra1Holder;

    private final LayoutPanel overlayExtra2Holder;

    private final LayoutPanel overlayExtra4Holder;

    private final ScrollPanel overlayExtra1ScrollPanel;

    public OverlayExtraHolder(ResponsiveLayoutPanel parent, String extra1Caption, String extra2Caption, String extra4Caption, AbstractDevConsole devConsole) {
        this.parent = parent;

        overlayDevConsoleHolder = new LayoutPanel();
        overlayDevConsoleHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        addTab(overlayDevConsoleHolder, "Extra");
        setTabVisible(getTabIndex(overlayDevConsoleHolder), false);

        overlayExtra1Holder = new LayoutPanel();
        overlayExtra1Holder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        overlayExtra1ScrollPanel = new ScrollPanel();
        overlayExtra1Holder.add(overlayExtra1ScrollPanel);
        addTab(overlayExtra1Holder, extra1Caption == null ? "" : extra1Caption);
        setTabVisible(getTabIndex(overlayExtra1Holder), false);

        overlayExtra2Holder = new LayoutPanel();
        overlayExtra2Holder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        addTab(overlayExtra2Holder, extra2Caption == null ? "" : extra2Caption);
        setTabVisible(getTabIndex(overlayExtra2Holder), false);

        overlayExtra4Holder = new LayoutPanel();
        overlayExtra4Holder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        addTab(overlayExtra4Holder, extra4Caption == null ? "" : extra4Caption);
        setTabVisible(getTabIndex(overlayExtra4Holder), false);

    }

    public void layout() {

        if (parent.getDisplay(DisplayType.devConsole).getWidget() != null) {
            setTabVisible(getTabIndex(overlayDevConsoleHolder), true);
            if (overlayDevConsoleHolder.getWidgetCount() == 0) {
                overlayDevConsoleHolder.add(parent.getDisplay(DisplayType.devConsole));
            }
        } else {
            setTabVisible(getTabIndex(overlayDevConsoleHolder), false);
        }

        switch (LayoutType.getLayoutType(Window.getClientWidth())) {

        case huge:
            setTabVisible(getTabIndex(overlayExtra1Holder), false);
            setTabVisible(getTabIndex(overlayExtra2Holder), false);
            setTabVisible(getTabIndex(overlayExtra4Holder), false);
            break;
        default:
            if (parent.getDisplay(DisplayType.extra1).getWidget() != null) {
                setTabVisible(getTabIndex(overlayExtra1Holder), true);
                if (overlayExtra1ScrollPanel.getWidget() == null) {
                    overlayExtra1ScrollPanel.setWidget(parent.getDisplay(DisplayType.extra1));
                }
            } else {
                setTabVisible(getTabIndex(overlayExtra1Holder), false);
            }

            if (parent.getDisplay(DisplayType.extra2).getWidget() != null) {
                setTabVisible(getTabIndex(overlayExtra2Holder), true);
                if (overlayExtra2Holder.getWidgetCount() == 0) {
                    overlayExtra2Holder.add(parent.getDisplay(DisplayType.extra2));
                }
            } else {
                setTabVisible(getTabIndex(overlayExtra2Holder), false);
            }

            if (parent.getDisplay(DisplayType.extra4).getWidget() != null) {
                setTabVisible(getTabIndex(overlayExtra4Holder), true);
                if (overlayExtra4Holder.getWidgetCount() == 0) {
                    overlayExtra4Holder.add(parent.getDisplay(DisplayType.extra4));
                }
            } else {
                setTabVisible(getTabIndex(overlayExtra4Holder), false);
            }
            break;
        }

    }

}
