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
 * Created on Apr 6, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class ViewImplBase extends DockLayoutPanel {

    private static final int TOOLBAR_DEFAULT_HEIGHT = 35;

    private final Label captionLabel;

    private final Toolbar headerToolbarOne;

    private final Toolbar headerToolbarTwo;

    private final Toolbar footerToolbar;

    private final SimplePanel headerToolbarOneHolder;

    private final SimplePanel headerToolbarTwoHolder;

    private final SimplePanel footerToolbarHolder;

    private int headerToolbarOneHeight = TOOLBAR_DEFAULT_HEIGHT;

    private int headerToolbarTwoHeight = TOOLBAR_DEFAULT_HEIGHT;

    private int footerToolbarHeight = TOOLBAR_DEFAULT_HEIGHT;

    public ViewImplBase() {
        super(Unit.PX);

        SimplePanel headerHolder = new SimplePanel();
        captionLabel = new Label();
        headerHolder.setWidget(captionLabel);
        headerHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.Header.name());
        addNorth(headerHolder, TOOLBAR_DEFAULT_HEIGHT);

        headerToolbarOneHolder = new SimplePanel();
        headerToolbarOneHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.HeaderToolbarOne.name());
        headerToolbarOne = new Toolbar();
        headerToolbarOneHolder.setWidget(headerToolbarOne);
        addNorth(headerToolbarOneHolder, 0);

        headerToolbarTwoHolder = new SimplePanel();
        headerToolbarTwoHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.HeaderToolbarTwo.name());
        headerToolbarTwo = new Toolbar();
        headerToolbarTwoHolder.setWidget(headerToolbarTwo);
        addNorth(headerToolbarTwoHolder, 0);

        footerToolbarHolder = new SimplePanel();
        footerToolbarHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.FooterToolbar.name());
        footerToolbar = new Toolbar();
        footerToolbarHolder.setWidget(footerToolbar);
        addSouth(footerToolbarHolder, 0);

    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    public void addHeaderToolbarOneItem(Widget widget) {
        if (headerToolbarOne.getWidgetCount() == 0) {
            setWidgetSize(headerToolbarOneHolder, headerToolbarOneHeight);
        }
        headerToolbarOne.addItem(widget);
    }

    public void addHeaderToolbarTwoItem(Widget widget) {
        if (headerToolbarTwo.getWidgetCount() == 0) {
            setWidgetSize(headerToolbarTwoHolder, headerToolbarTwoHeight);
        }
        headerToolbarTwo.addItem(widget, true);
    }

    public void addFooterToolbarItem(Widget widget) {
        if (footerToolbar.getWidgetCount() == 0) {
            setWidgetSize(footerToolbarHolder, footerToolbarHeight);
        }
        footerToolbar.addItem(widget);
    }

    public void setHeaderToolbarOneHeight(int headerToolbarOneHeight) {
        this.headerToolbarOneHeight = headerToolbarOneHeight;
        if (headerToolbarOne.getWidgetCount() == 0) {
            setWidgetSize(headerToolbarOneHolder, headerToolbarOneHeight);
        }
    }

    public void setHeaderToolbarTwoHeight(int headerToolbarTwoHeight) {
        this.headerToolbarTwoHeight = headerToolbarTwoHeight;
        if (headerToolbarTwo.getWidgetCount() == 0) {
            setWidgetSize(headerToolbarTwoHolder, headerToolbarTwoHeight);
        }
    }

    public void setFooterToolbarHeight(int footerToolbarHeight) {
        this.footerToolbarHeight = footerToolbarHeight;
        if (footerToolbar.getWidgetCount() == 0) {
            setWidgetSize(footerToolbarHolder, footerToolbarHeight);
        }
    }
}