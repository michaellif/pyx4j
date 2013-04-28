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
 * Created on Apr 22, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutCommand;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.DisplayPanel;

public class RiaLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize {

    private final Layout layout;

    private final LayoutCommand layoutCmd;

    private final DisplayPanel headerDisplay;

    private final DisplayPanel notificationsDisplay;

    //TODO leave navigDisplay and move shortcuts and footer under it
    private final DisplayPanel navigDisplay;

    private final DisplayPanel shortcutsDisplay;

    private final DisplayPanel footerDisplay;

    private final DisplayPanel contentDisplay;

    private final DockLayoutPanel menuPanel;

    private boolean menuVisible;

    private int menuWidth = 20;

    private int headerHeight = 50;

    private int notificationsHeight = 0;

    public RiaLayoutPanel() {
        setElement(Document.get().createDivElement());

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

        // ============ Notifications ============
        {
            notificationsDisplay = new DisplayPanel();
            Layer layer = layout.attachChild(notificationsDisplay.asWidget().getElement(), notificationsDisplay);
            notificationsDisplay.setLayoutData(layer);

            getChildren().add(notificationsDisplay);
            adopt(notificationsDisplay);
        }

        // ============ Menu ============
        {
            menuPanel = new DockLayoutPanel(Unit.PX);

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

    @Deprecated
    //TODO combine with NavigDisplay
    public DisplayPanel getShortcutsDisplay() {
        return shortcutsDisplay;
    }

    @Deprecated
    //TODO combine with NavigDisplay
    public DisplayPanel getFooterDisplay() {
        return footerDisplay;
    }

    public DisplayPanel getContentDisplay() {
        return contentDisplay;
    }

    public DisplayPanel getNotificationsDisplay() {
        return notificationsDisplay;
    }

    public void forceLayout() {
        layoutCmd.cancel();
        doLayout();
        layout.layout();
        onResize();
    }

    private void doLayout() {

        int top = 0;
        int height = headerHeight;

        {
            Layer layer = (Layer) headerDisplay.getLayoutData();
            layer.setTopHeight(top, Unit.PX, height, Unit.PX);
            layer.setLeftWidth(0.0, Unit.PX, 100.0, Unit.PCT);
        }

        top += height;
        height = notificationsHeight;

        {
            Layer layer = (Layer) notificationsDisplay.getLayoutData();
            layer.setTopHeight(top, Unit.PX, height, Unit.PX);
            layer.setLeftWidth(0.0, Unit.PX, 100.0, Unit.PCT);
        }

        top += height;

        {
            Layer layer = (Layer) menuPanel.getLayoutData();
            layer.setVisible(menuVisible);
            if (menuVisible) {
                layer.setTopBottom(top, Unit.PX, 0, Unit.PX);
                layer.setLeftWidth(0, Unit.PX, menuWidth, Unit.PX);
            }
        }

        {
            Layer layer = (Layer) contentDisplay.getLayoutData();
            layer.setTopBottom(top, Unit.PX, 0, Unit.PX);
            layer.setLeftRight(menuVisible ? menuWidth : 0, Unit.PX, 0, Unit.PX);
        }
    }

    public void setMenuVisible(boolean visible) {
        this.menuVisible = visible;
    }

    public void setMenuWidth(int width) {
        this.menuWidth = width;
    }

    public void setHeaderHeight(int height) {
        this.headerHeight = height;
    }

    public void setNotificationsHeight(int height) {
        this.notificationsHeight = height;
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

        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }

}
