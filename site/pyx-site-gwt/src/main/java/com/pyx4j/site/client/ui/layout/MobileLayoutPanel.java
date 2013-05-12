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
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.DisplayPanel;

public class MobileLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize {

    private static final int ANIMATION_TIME = 500;

    private final Layout layout;

    private final DisplayPanel menuDisplay;

    private final DisplayPanel screenDisplay;

    private final DisplayPanel alertsDisplay;

    private final DisplayPanel alertsInfoDisplay;

    private final MobileScreenLayoutPanel screenContent;

    private boolean shiftMenu = false;

    private boolean shiftAlerts = false;

    private boolean alertsInfoVisible = false;

    public MobileLayoutPanel() {
        setElement(Document.get().createDivElement());

        layout = new Layout(getElement());

        // ============ Menu ============
        {
            menuDisplay = new DisplayPanel();
            menuDisplay.setStyleName(MobileLayoutPanelTheme.StyleName.MenuScreen.name());

            Layer layer = layout.attachChild(menuDisplay.asWidget().getElement(), menuDisplay);
            menuDisplay.setLayoutData(layer);

            getChildren().add(menuDisplay);
            adopt(menuDisplay);
        }

        // ============ Screen ============
        {
            screenDisplay = new DisplayPanel();
            Layer layer = layout.attachChild(screenDisplay.asWidget().getElement(), screenDisplay);
            screenDisplay.setLayoutData(layer);

            getChildren().add(screenDisplay);
            adopt(screenDisplay);
        }

        // ============ Alerts ============
        {
            alertsDisplay = new DisplayPanel();
            alertsDisplay.setStyleName(MobileLayoutPanelTheme.StyleName.AlertsScreen.name());

            Layer layer = layout.attachChild(alertsDisplay.asWidget().getElement(), alertsDisplay);
            alertsDisplay.setLayoutData(layer);

            getChildren().add(alertsDisplay);
            adopt(alertsDisplay);
        }

        // ============ Alerts Info ============
        {
            alertsInfoDisplay = new DisplayPanel();
            //alertsInfoDisplay.setStyleName(MobileLayoutPanelTheme.StyleName.AlertsScreen.name());

            Layer layer = layout.attachChild(alertsInfoDisplay.asWidget().getElement(), alertsInfoDisplay);
            alertsInfoDisplay.setLayoutData(layer);

            getChildren().add(alertsInfoDisplay);
            adopt(alertsInfoDisplay);
        }
        // ============ ScreenContent ============
        {
            screenContent = new MobileScreenLayoutPanel();
            screenContent.setSize("100%", "100%");
        }

        forceLayout();
    }

    public DisplayPanel getHeaderDisplay() {
        return screenContent.getHeaderDisplay();
    }

    public DisplayPanel getListerDisplay() {
        return screenContent.getListerDisplay();
    }

    public DisplayPanel getDetailsDisplay() {
        return screenContent.getDetailsDisplay();
    }

    public DisplayPanel getAlertsInfoDisplay() {
        return alertsInfoDisplay;
    }

    public DisplayPanel getScreenDisplay() {
        return screenDisplay;
    }

    public DisplayPanel getMenuDisplay() {
        return menuDisplay;
    }

    public DisplayPanel getAlertsDisplay() {
        return alertsDisplay;
    }

    public void forceLayout() {
        doLayout();
        layout.layout(ANIMATION_TIME);
        onResize();
    }

    private void doLayout() {

        Layer menuLayer = (Layer) menuDisplay.getLayoutData();
        Layer screenLayer = (Layer) screenDisplay.getLayoutData();
        Layer alertsLayer = (Layer) alertsDisplay.getLayoutData();
        Layer alertsInfoLayer = (Layer) alertsInfoDisplay.getLayoutData();

        menuLayer.setLeftWidth(-75.0, Unit.PCT, 75.0, Unit.PCT);
        screenLayer.setLeftWidth(0.0, Unit.PCT, 100.0, Unit.PCT);
        alertsLayer.setRightWidth(-75.0, Unit.PCT, 75.0, Unit.PCT);

        alertsInfoLayer.setBottomHeight(0.0, Unit.PX, 50.0, Unit.PX);
        alertsInfoLayer.setRightWidth(0.0, Unit.PX, 50.0, Unit.PX);
        alertsInfoDisplay.setVisible(alertsInfoVisible);

        if (shiftMenu) {
            menuLayer.setLeftWidth(0.0, Unit.PCT, 75.0, Unit.PCT);
            screenLayer.setLeftWidth(75.0, Unit.PCT, 100.0, Unit.PCT);
            alertsInfoLayer.setRightWidth(-75.0, Unit.PCT, 20.0, Unit.PX);
        }

        if (shiftAlerts) {
            alertsLayer.setRightWidth(0.0, Unit.PCT, 75.0, Unit.PCT);
            screenLayer.setRightWidth(75.0, Unit.PCT, 100.0, Unit.PCT);
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

    public void showApplicationContent() {
        screenDisplay.setWidget(screenContent);
        forceLayout();
    }

    public void shiftMenu() {
        this.shiftMenu = !shiftMenu;
        forceLayout();
    }

    public void shiftAlerts() {
        this.shiftAlerts = !shiftAlerts;
        forceLayout();
    }

    public void showAlerts(boolean visible) {
        this.alertsInfoVisible = visible;
        forceLayout();
    }

    public void setListerLayout(boolean listerLayout) {
        screenContent.setListerLayout(listerLayout);
        alertsInfoDisplay.setVisible(!listerLayout && alertsInfoVisible);
    }

    public void setPageOrientation(PageOrientation pageOrientation) {
        screenContent.setPageOrientation(pageOrientation);
    }

    public void expandDetails(boolean expandDetails) {
        screenContent.expandDetails(expandDetails);
    }

}
