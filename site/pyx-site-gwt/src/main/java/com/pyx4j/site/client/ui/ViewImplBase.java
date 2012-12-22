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
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.resources.SiteImages;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.site.client.ui.crud.misc.MementoImpl;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public abstract class ViewImplBase extends DockLayoutPanel implements IView {

    private static final I18n i18n = I18n.get(ViewImplBase.class);

    private static final int TOOLBAR_DEFAULT_HEIGHT = 35;

    private final VisorLayoutPanel visorPane;

    private final Label captionLabel;

    private final Toolbar headerToolbar;

    private final Toolbar footerToolbar;

    private final FlowPanel headerContainer;

    private final SimplePanel headerBreadcrumbHolder;

    private final SimplePanel headerToolbarHolder;

    private final SimplePanel footerToolbarHolder;

    private final int headerToolbarHeight = TOOLBAR_DEFAULT_HEIGHT;

    private int footerToolbarHeight = TOOLBAR_DEFAULT_HEIGHT;

    private final IMemento memento = new MementoImpl();

    public ViewImplBase() {
        super(Unit.PX);

        SimplePanel headerHolder = new SimplePanel();
        captionLabel = new Label();
        headerHolder.setWidget(captionLabel);
        headerHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.Header.name());
        addNorth(headerHolder, TOOLBAR_DEFAULT_HEIGHT);

        headerContainer = new FlowPanel();
        headerContainer.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.HeaderContainer.name());
        addNorth(headerContainer, 0);

        headerToolbarHolder = new SimplePanel();
        headerToolbarHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.HeaderToolbar.name());
        headerToolbar = new Toolbar();
        headerToolbarHolder.setWidget(headerToolbar);

        headerContainer.add(headerToolbarHolder);

        headerBreadcrumbHolder = new SimplePanel();
        headerBreadcrumbHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.HeaderBreadcrumbs.name());
        headerContainer.add(headerBreadcrumbHolder);

        footerToolbarHolder = new SimplePanel();
        footerToolbarHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.FooterToolbar.name());
        footerToolbar = new Toolbar();
        footerToolbarHolder.setWidget(footerToolbar);
        addSouth(footerToolbarHolder, 0);

        visorPane = new VisorLayoutPanel();
        visorPane.setAnimationDuration(500);
    }

    protected IsWidget getContentPane() {
        if (visorPane.getWidgetCount() == 0) {
            return null;
        }
        return visorPane.getWidget(0);
    }

    protected void setContentPane(IsWidget widget) {
        assert visorPane.getWidgetCount() == 0 : "Content Pane is already set";
        visorPane.setContentPane(widget);
        add(visorPane);
    }

    @Override
    public void showVisor(IsWidget widget, String caption) {
        DockLayoutPanel visorHolder = new DockLayoutPanel(Unit.EM);
        visorHolder.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.Visor.name());

        FlowPanel header = new FlowPanel();
        header.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.VisorHeader.name());
        visorHolder.addNorth(header, 3);

        Button backButton = new Button(new Image(SiteImages.INSTANCE.backButton()), i18n.tr("Back"), new Command() {
            @Override
            public void execute() {
                hideVisor();
            }
        });
        backButton.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.VisorBackButton.name());
        header.add(backButton);

        Label captionLabel = new Label(caption);
        captionLabel.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.VisorCaption.name());
        header.add(captionLabel);

        visorHolder.add(widget.asWidget());

        visorPane.showVisorPane(visorHolder);
    }

    @Override
    public void hideVisor() {
        visorPane.hideVisorPane();
    }

    @Override
    public boolean isVisorShown() {
        return visorPane.isVisorShown();
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

    @Override
    public IMemento getMemento() {
        return memento;
    }

    @Override
    public void storeState(Place place) {
        memento.setCurrentPlace(place);
    }

    @Override
    public void restoreState() {
    }
}
