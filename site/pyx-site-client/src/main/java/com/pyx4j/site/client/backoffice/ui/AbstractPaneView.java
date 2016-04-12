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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.IPaneView.IPanePresenter;
import com.pyx4j.site.client.ui.layout.LayoutSystem;
import com.pyx4j.widgets.client.HasSecureConcern;
import com.pyx4j.widgets.client.Toolbar;

public abstract class AbstractPaneView<PRESENTER extends IPanePresenter> implements IPaneView<PRESENTER> {

    private static final double TOOLBAR_DEFAULT_HEIGHT = 34;

    private final LayoutSystem layoutSystem;

    private final AbstractPaneViewLayout layoutWidget;

    private final Label captionLabel;

    private final Toolbar headerToolbar;

    private final Toolbar headerToolbarLeft;

    private final Toolbar footerToolbar;

    private final FlowPanel headerContainer;

    private final SimplePanel headerBreadcrumbHolder;

    private final SimplePanel headerToolbarHolder;

    private final SimplePanel headerToolbarHolderLeft;

    private final SimplePanel footerToolbarHolder;

    private final FlowPanel headerCaption;

    private PRESENTER presenter;

    public AbstractPaneView(LayoutSystem layoutSystem) {
        this.layoutSystem = layoutSystem;
        switch (layoutSystem) {
        case BasicPanels:
            layoutWidget = new AbstractPaneViewLayoutBasicPanels();
            break;
        case LayoutPanels:
            layoutWidget = new AbstractPaneViewLayoutLayoutPanels();
            break;
        default:
            throw new Error(layoutSystem.name());
        }

        headerCaption = new FlowPanel();
        captionLabel = new Label();
        captionLabel.setStyleName(PaneTheme.StyleName.HeaderCaptionLabel.name());
        headerCaption.add(captionLabel);
        headerCaption.setStyleName(PaneTheme.StyleName.HeaderCaption.name());
        layoutWidget.addNorth(headerCaption, TOOLBAR_DEFAULT_HEIGHT);

        headerContainer = new FlowPanel();
        headerContainer.setStyleName(PaneTheme.StyleName.HeaderContainer.name());
        headerContainer.setWidth("100%");
        layoutWidget.addNorth(headerContainer, 0);

        headerToolbarHolder = new SimplePanel();
        headerToolbarHolder.setStyleName(PaneTheme.StyleName.HeaderToolbar.name());
        headerToolbar = new Toolbar();
        headerToolbarHolder.setWidget(headerToolbar);
        headerContainer.add(headerToolbarHolder);

        headerBreadcrumbHolder = new SimplePanel();
        headerBreadcrumbHolder.setStyleName(PaneTheme.StyleName.HeaderBreadcrumbs.name());
        headerContainer.add(headerBreadcrumbHolder);

        headerToolbarLeft = new Toolbar();
        headerToolbarHolderLeft = new SimplePanel();
        headerToolbarHolderLeft.setStyleName(PaneTheme.StyleName.HeaderToolbarLeft.name());
        headerToolbarHolderLeft.addStyleName(PaneTheme.StyleName.HeaderToolbar.name());
        headerToolbarHolderLeft.setWidget(headerToolbarLeft);
        headerContainer.add(headerToolbarHolderLeft);

        footerToolbarHolder = new SimplePanel();
        footerToolbarHolder.setStyleName(PaneTheme.StyleName.FooterToolbar.name());
        footerToolbar = new Toolbar();
        footerToolbarHolder.setWidget(footerToolbar);
        layoutWidget.addSouth(footerToolbarHolder, 0);

    }

    // ---- Layout delegation begin

    protected LayoutSystem getLayoutSystem() {
        return layoutSystem;
    }

    @Override
    public Widget asWidget() {
        return layoutWidget.asWidget();
    }

    protected void setCenter(Widget widget) {
        layoutWidget.setCenter(widget);
    }

    protected com.google.gwt.dom.client.Element getElement() {
        return asWidget().getElement();
    }

    protected void setStyleName(String style) {
        asWidget().setStyleName(style);
    }

    protected void setSize(String width, String height) {
        asWidget().setSize(width, height);
    }

    // ---- Layout delegation end

    protected Collection<HasSecureConcern> secureConcerns() {
        return Arrays.<HasSecureConcern> asList(headerToolbar, footerToolbar);
    }

    protected FlowPanel getHeaderCaption() {
        return headerCaption;
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
        AppSite.instance().setWindowTitle(caption);
    }

    public String getCaption() {
        return captionLabel.getText();
    }

    public void setBreadcrumbsBar(BreadcrumbsBar breadcrumbsBar) {
        layoutWidget.setWidgetHeight(headerContainer, TOOLBAR_DEFAULT_HEIGHT);
        headerBreadcrumbHolder.setWidget(breadcrumbsBar);
    }

    public void addHeaderToolbarItem(Widget widget) {
        layoutWidget.setWidgetHeight(headerContainer, TOOLBAR_DEFAULT_HEIGHT);
        headerToolbar.addItem(widget);
    }

    public void addHeaderToolbarItemLeft(Widget widget) {
        layoutWidget.setWidgetHeight(headerContainer, TOOLBAR_DEFAULT_HEIGHT);
        headerToolbarLeft.addItem(widget);
    }

    public void addFooterToolbarItem(Widget widget) {
        layoutWidget.setWidgetHeight(footerToolbarHolder, TOOLBAR_DEFAULT_HEIGHT);
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
