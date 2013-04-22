/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 20, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutCommand;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.DisplayPanel;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;

public class CrmLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize {

    private final int animationDuration = 500;

    private final Layout layout;

    private final LayoutCommand layoutCmd;

    private final DisplayPanel headerDisplay;

    private final DisplayPanel navigDisplay;

    private final DisplayPanel shortcutsDisplay;

    private final DisplayPanel footerDisplay;

    private final DisplayPanel contentDisplay;

    private final DockLayoutPanel menuPanel;

    private boolean menuVisible;

    private boolean menuExpanded;

    public CrmLayoutPanel() {
        setElement(Document.get().createDivElement());
        setStyleName(CrmSitePanelTheme.StyleName.SiteViewContent.name());

        layout = new Layout(getElement());
        layoutCmd = new DisplaysLayoutCommand();

        // ============ Header ============
        {
            headerDisplay = new DisplayPanel();
            Layer layer = layout.attachChild(headerDisplay.asWidget().getElement(), headerDisplay);
            headerDisplay.setLayoutData(layer);

            getChildren().add(headerDisplay);
            adopt(headerDisplay);
        }

        // ============ Menu ============
        {
            menuPanel = new DockLayoutPanel(Unit.PX);
            menuPanel.setStyleName(CrmSitePanelTheme.StyleName.SiteViewNavigContainer.name());

            Layer layer = layout.attachChild(menuPanel.asWidget().getElement(), menuPanel);
            menuPanel.setLayoutData(layer);

            getChildren().add(menuPanel);
            adopt(menuPanel);

            footerDisplay = new DisplayPanel();
            menuPanel.addSouth(footerDisplay, 40);

            shortcutsDisplay = new DisplayPanel();
            menuPanel.addSouth(shortcutsDisplay, 200);

            navigDisplay = new DisplayPanel();
            menuPanel.add(navigDisplay);

        }

        // ============ Content ============
        {
            contentDisplay = new DisplayPanel();

            Layer layer = layout.attachChild(contentDisplay.asWidget().getElement(), contentDisplay);
            contentDisplay.setLayoutData(layer);

            getChildren().add(contentDisplay);
            adopt(contentDisplay);
        }

        forceLayout();
    }

    public DisplayPanel getHeaderDisplay() {
        return headerDisplay;
    }

    public DisplayPanel getNavigDisplay() {
        return navigDisplay;
    }

    public DisplayPanel getShortcutsDisplay() {
        return shortcutsDisplay;
    }

    public DisplayPanel getFooterDisplay() {
        return footerDisplay;
    }

    public DisplayPanel getContentDisplay() {
        return contentDisplay;
    }

    public void forceLayout() {
        layoutCmd.cancel();
        doLayout();
        layout.layout();
        onResize();
    }

    private void doLayout() {

        double menuWidth = menuExpanded ? 200 : 150;

        Layer headerLayer = (Layer) headerDisplay.getLayoutData();
        Layer menuLayer = (Layer) menuPanel.getLayoutData();
        Layer contentLayer = (Layer) contentDisplay.getLayoutData();

        headerLayer.setTopHeight(0.0, Unit.PCT, 5, Unit.EM);
        headerLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);

        if (menuVisible) {
            menuLayer.setTopBottom(5, Unit.EM, 0, Unit.EM);
            menuLayer.setLeftWidth(0, Unit.EM, menuWidth, Unit.PX);
            contentLayer.setLeftRight(menuWidth, Unit.PX, 0, Unit.EM);
            menuLayer.setVisible(true);
        } else {
            contentLayer.setLeftRight(0, Unit.EM, 0, Unit.EM);
            menuLayer.setVisible(false);
        }

        contentLayer.setTopBottom(5, Unit.EM, 0, Unit.EM);

    }

    public void setMenuVisible(boolean visible) {
        this.menuVisible = visible;
        forceLayout();
    }

    protected void setMenuExpanded(boolean expanded) {
        this.menuExpanded = expanded;
        forceLayout();
    }

    private class DisplaysLayoutCommand extends LayoutCommand {
        public DisplaysLayoutCommand() {
            super(layout);
        }

        @Override
        public void schedule(int duration, final AnimationCallback callback) {
        }

        @Override
        protected void doBeforeLayout() {
            doLayout();
        }
    }

    @Override
    public void onResize() {

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                setMenuExpanded(Window.getClientWidth() > 1000);
            }
        });

        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }

}