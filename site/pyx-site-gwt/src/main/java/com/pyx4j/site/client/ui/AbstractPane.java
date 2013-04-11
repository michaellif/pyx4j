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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.ui.visor.IVisor;
import com.pyx4j.site.client.ui.visor.VisorHolder;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public abstract class AbstractPane extends DockLayoutPanel implements IPane {

    private static final int TOOLBAR_DEFAULT_HEIGHT = 35;

    private final VisorHolder visorHolder;

    private final Label captionLabel;

    private final Toolbar headerToolbar;

    private final Toolbar footerToolbar;

    private final FlowPanel headerContainer;

    private final SimplePanel headerBreadcrumbHolder;

    private final SimplePanel headerToolbarHolder;

    private final SimplePanel footerToolbarHolder;

    private final FlowPanel headerCaption;

    private final int headerToolbarHeight = TOOLBAR_DEFAULT_HEIGHT;

    private int footerToolbarHeight = TOOLBAR_DEFAULT_HEIGHT;

    public AbstractPane() {
        super(Unit.PX);

        headerCaption = new FlowPanel();
        captionLabel = new Label();
        captionLabel.setStyleName(DefaultPaneTheme.StyleName.HeaderCaption.name());
        headerCaption.add(captionLabel);
        headerCaption.setStyleName(DefaultPaneTheme.StyleName.Header.name());
        addNorth(headerCaption, TOOLBAR_DEFAULT_HEIGHT);

        headerContainer = new FlowPanel();
        headerContainer.setStyleName(DefaultPaneTheme.StyleName.HeaderContainer.name());
        addNorth(headerContainer, 0);

        headerToolbarHolder = new SimplePanel();
        headerToolbarHolder.setStyleName(DefaultPaneTheme.StyleName.HeaderToolbar.name());
        headerToolbar = new Toolbar();
        headerToolbarHolder.setWidget(headerToolbar);

        headerContainer.add(headerToolbarHolder);

        headerBreadcrumbHolder = new SimplePanel();
        headerBreadcrumbHolder.setStyleName(DefaultPaneTheme.StyleName.HeaderBreadcrumbs.name());
        headerContainer.add(headerBreadcrumbHolder);

        footerToolbarHolder = new SimplePanel();
        footerToolbarHolder.setStyleName(DefaultPaneTheme.StyleName.FooterToolbar.name());
        footerToolbar = new Toolbar();
        footerToolbarHolder.setWidget(footerToolbar);
        addSouth(footerToolbarHolder, 0);

        visorHolder = new VisorHolder(this);
        visorHolder.setAnimationDuration(500);
    }

    protected FlowPanel getHeaderCaption() {
        return headerCaption;
    }

    protected IsWidget getContentPane() {
        if (visorHolder.getWidgetCount() == 0) {
            return null;
        }
        return visorHolder.getWidget(0);
    }

    protected void setContentPane(IsWidget widget) {
        assert visorHolder.getWidgetCount() == 0 : "Content Pane is already set";
        visorHolder.setContentPane(widget);
        add(visorHolder);
    }

    @Override
    public void showVisor(IVisor visor) {
        visorHolder.showVisorPane(visor);
    }

    @Override
    public void hideVisor() {
        visorHolder.hideVisor();
    }

    @Override
    public boolean isVisorShown() {
        return visorHolder.isVisorShown();
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    public String getCaption() {
        return captionLabel.getText();
    }

    public void setBreadcrumbsBar(BreadcrumbsBar breadcrumbsBar) {
        setWidgetSize(headerContainer, headerToolbarHeight);
        headerBreadcrumbHolder.setWidget(breadcrumbsBar);
    }

    public void addHeaderToolbarItem(Widget widget) {
        setWidgetSize(headerContainer, headerToolbarHeight);
        headerToolbar.addItem(widget);
    }

    public void addFooterToolbarItem(Widget widget) {
        setWidgetSize(footerToolbarHolder, footerToolbarHeight);
        footerToolbar.addItem(widget);
    }

    public void setFooterToolbarHeight(int footerToolbarHeight) {
        this.footerToolbarHeight = footerToolbarHeight;
        if (footerToolbar.getWidgetCount() == 0) {
            setWidgetSize(footerToolbarHolder, footerToolbarHeight);
        }
    }

}
