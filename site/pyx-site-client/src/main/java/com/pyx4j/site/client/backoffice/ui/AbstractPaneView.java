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
 */
package com.pyx4j.site.client.backoffice.ui;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.backoffice.ui.IPaneView.IPanePresenter;
import com.pyx4j.widgets.client.HasSecureConcern;
import com.pyx4j.widgets.client.Toolbar;

public abstract class AbstractPaneView<PRESENTER extends IPanePresenter> extends DockLayoutPanel implements IPaneView<PRESENTER> {

    private static final double TOOLBAR_DEFAULT_HEIGHT = 34;

    private final Label captionLabel;

    private final Toolbar headerToolbar;

    private final Toolbar footerToolbar;

    private final FlowPanel headerContainer;

    private final SimplePanel headerBreadcrumbHolder;

    private final SimplePanel headerToolbarHolder;

    private final SimplePanel footerToolbarHolder;

    private final FlowPanel headerCaption;

    private PRESENTER presenter;

    public AbstractPaneView() {
        super(Unit.PX);

        headerCaption = new FlowPanel();
        captionLabel = new Label();
        captionLabel.setStyleName(PaneTheme.StyleName.HeaderCaptionLabel.name());
        headerCaption.add(captionLabel);
        headerCaption.setStyleName(PaneTheme.StyleName.HeaderCaption.name());
        addNorth(headerCaption, TOOLBAR_DEFAULT_HEIGHT);

        headerContainer = new FlowPanel();
        headerContainer.setStyleName(PaneTheme.StyleName.HeaderContainer.name());
        addNorth(headerContainer, 0);

        headerToolbarHolder = new SimplePanel();
        headerToolbarHolder.setStyleName(PaneTheme.StyleName.HeaderToolbar.name());
        headerToolbar = new Toolbar();
        headerToolbarHolder.setWidget(headerToolbar);

        headerContainer.add(headerToolbarHolder);

        headerBreadcrumbHolder = new SimplePanel();
        headerBreadcrumbHolder.setStyleName(PaneTheme.StyleName.HeaderBreadcrumbs.name());
        headerContainer.add(headerBreadcrumbHolder);

        footerToolbarHolder = new SimplePanel();
        footerToolbarHolder.setStyleName(PaneTheme.StyleName.FooterToolbar.name());
        footerToolbar = new Toolbar();
        footerToolbarHolder.setWidget(footerToolbar);
        addSouth(footerToolbarHolder, 0);

    }

    protected Collection<HasSecureConcern> secureConcerns() {
        return Arrays.<HasSecureConcern> asList(headerToolbar, footerToolbar);
    }

    protected FlowPanel getHeaderCaption() {
        return headerCaption;
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    public String getCaption() {
        return captionLabel.getText();
    }

    public void setBreadcrumbsBar(BreadcrumbsBar breadcrumbsBar) {
        setWidgetSize(headerContainer, TOOLBAR_DEFAULT_HEIGHT);
        headerBreadcrumbHolder.setWidget(breadcrumbsBar);
    }

    public void addHeaderToolbarItem(Widget widget) {
        setWidgetSize(headerContainer, TOOLBAR_DEFAULT_HEIGHT);
        headerToolbar.addItem(widget);
    }

    public void addFooterToolbarItem(Widget widget) {
        setWidgetSize(footerToolbarHolder, TOOLBAR_DEFAULT_HEIGHT);
        footerToolbar.addItem(widget);
    }

    @Override
    public PRESENTER getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(PRESENTER presenter) {
        this.presenter = presenter;
    }
}
