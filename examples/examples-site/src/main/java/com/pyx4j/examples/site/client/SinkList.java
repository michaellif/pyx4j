/*
 * Copyright 2007 Google Inc.
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
 */
package com.pyx4j.examples.site.client;

import java.util.ArrayList;


import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.examples.site.client.Page.PageInfo;

/**
 * The left panel that contains all of the sinks, along with a short description of each.
 */
public class SinkList extends Composite {

    static final int NORMAL = 0;

    static final int SELECTED = 1;

    static final int MOUSE_OVER = 2;

    private final HorizontalPanel list = new HorizontalPanel();

    private final ArrayList<PageInfo> pages = new ArrayList<PageInfo>();

    private int selectedPage = -1;

    public SinkList() {
        initWidget(list);
    }

    private class TabHyperlink extends Hyperlink {

        private final int index;

        public TabHyperlink(String targetHistoryToken, int index) {
            super("", true, targetHistoryToken);
            this.index = index;

            DOM.setStyleAttribute(getElement(), "cursor", "pointer");
            DOM.setStyleAttribute(getElement(), "cursor", "hand");
            DOM.setStyleAttribute(getElement(), "position", "relative");
            DOM.setStyleAttribute(getElement(), "top", "-61px");

            sinkEvents(Event.MOUSEEVENTS);
        }

        @Override
        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                if (selectedPage != index) {
                    styleTabLink(index, MOUSE_OVER);
                }
                break;

            case Event.ONMOUSEOUT:
                if (selectedPage == index) {
                    styleTabLink(index, SELECTED);
                } else {
                    styleTabLink(index, NORMAL);
                }
                break;
            }

            super.onBrowserEvent(event);
        }

        public void setStyle(int styleType) {
            switch (styleType) {
            case NORMAL:
                DOM.setStyleAttribute(getElement(), "cursor", "pointer");
                DOM.setStyleAttribute(getElement(), "cursor", "hand");
                break;
            case SELECTED:
                DOM.setStyleAttribute(getElement(), "cursor", "default");
                break;
            default:
                break;
            }
        }

    }

    private class TabLink extends AbsolutePanel {

        private static final int FONT_SIZE = 15;

        private final TabHyperlink hyperlink;

        private final Image background;

        private final Image text;

        private final int imageLeft;

        private final int imageTop;

        public TabLink(String name, int index, int imageLeft, int imageTop) {
            super();

            this.imageLeft = imageLeft;
            this.imageTop = imageTop;

            DOM.setStyleAttribute(this.getElement(), "margin", "10px");

            background = new Image(Site.getSiteImageBundle().curiousKidsImages());
            add(background);
            background.setPixelSize(103, 96);

            text = new Image(Site.getSiteImageBundle().curiousKidsImages());
            DOM.setStyleAttribute(text.getElement(), "position", "relative");
            DOM.setStyleAttribute(text.getElement(), "top", "-32px");
            DOM.setStyleAttribute(text.getElement(), "left", "12px");
            add(text);
            text.setPixelSize(103, FONT_SIZE);

            hyperlink = new TabHyperlink(name, index);
            add(hyperlink);

            DOM.setStyleAttribute(hyperlink.getElement(), "height", "47px");

            setPixelSize(103, 96);

            setStyle(NORMAL);
        }

        public void setStyle(int styleType) {
            switch (styleType) {
            case NORMAL:
                background.setVisibleRect(0, 121, 103, 96);
                text.setVisibleRect(imageLeft + 12, imageTop + 97, 79, 14);
                break;
            case SELECTED:
                background.setVisibleRect(imageLeft, imageTop, 103, 96);
                text.setVisibleRect(imageLeft + 12, imageTop + 97 + FONT_SIZE, 79, 14);
                break;
            case MOUSE_OVER:
                background.setVisibleRect(imageLeft, imageTop, 103, 96);
                text.setVisibleRect(imageLeft + 12, imageTop + 97 + FONT_SIZE * 2, 79, 14);
                break;
            default:
                break;
            }

            hyperlink.setStyle(styleType);
        }
    }

    public void addPage(final PageInfo info, int imageLeft, int imageTop) {
        String name = info.getTabName();
        int index = list.getWidgetCount();

        TabLink link = new TabLink(name, index, imageLeft, imageTop);
        list.add(link);
        list.setCellHeight(link, "116px");
        list.setCellWidth(link, "123px");
        list.setCellVerticalAlignment(link, HorizontalPanel.ALIGN_BOTTOM);

        pages.add(info);

    }

    public PageInfo find(String pageName) {
        for (int i = 0; i < pages.size(); ++i) {
            PageInfo info = pages.get(i);
            if (info.getTabName().equals(pageName)) {
                return info;
            }
        }

        return null;
    }

    public void setTabSelection(String tabName) {
        if (selectedPage != -1) {
            styleTabLink(selectedPage, NORMAL);
        }

        for (int i = 0; i < pages.size(); ++i) {
            PageInfo info = pages.get(i);
            if (info.getTabName().equals(tabName)) {
                selectedPage = i;
                styleTabLink(selectedPage, SELECTED);
                return;
            }
        }
    }

    private void styleTabLink(int index, int style) {
        ((TabLink) list.getWidget(index)).setStyle(style);
    }
}
