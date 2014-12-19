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
 */
package com.pyx4j.tester.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.LayoutCommand;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class TesterLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize {

    private final Layout layout;

    private final LayoutCommand layoutCmd;

    private final DisplayPanel navigDisplay;

    private final DisplayPanel consoleDisplay;

    private final DisplayPanel contentDisplay;

    private boolean consoleVisible;

    public TesterLayoutPanel() {
        setElement(Document.get().createDivElement());

        layout = new Layout(getElement());
        layoutCmd = new DisplaysLayoutCommand();

        // ============ navigDisplay ============
        {
            navigDisplay = new DisplayPanel(DisplayType.header);
            Layer layer = layout.attachChild(navigDisplay.asWidget().getElement(), navigDisplay);
            navigDisplay.setLayoutData(layer);

            getChildren().add(navigDisplay);
            adopt(navigDisplay);
        }

        // ============ Content ============
        {
            contentDisplay = new DisplayPanel(DisplayType.content);
            Layer layer = layout.attachChild(contentDisplay.asWidget().getElement(), contentDisplay);
            contentDisplay.setLayoutData(layer);

            getChildren().add(contentDisplay);
            adopt(contentDisplay);
        }

        // ============ consoleDisplay ============
        {
            consoleDisplay = new DisplayPanel(DisplayType.footer);
            Layer layer = layout.attachChild(consoleDisplay.asWidget().getElement(), consoleDisplay);
            consoleDisplay.setLayoutData(layer);

            getChildren().add(consoleDisplay);
            adopt(consoleDisplay);
        }

        forceLayout();
    }

    public DisplayPanel getNavigDisplay() {
        return navigDisplay;
    }

    public DisplayPanel getContentDisplay() {
        return contentDisplay;
    }

    public DisplayPanel getConsoleDisplay() {
        return consoleDisplay;
    }

    public void forceLayout() {
        layoutCmd.cancel();
        doLayout();
        layout.layout();
        onResize();
    }

    private void doLayout() {

        Layer navigLayer = (Layer) navigDisplay.getLayoutData();
        Layer contentLayer = (Layer) contentDisplay.getLayoutData();
        Layer consoleLayer = (Layer) consoleDisplay.getLayoutData();

        navigLayer.setTopHeight(0.0, Unit.PCT, 100.0, Unit.PCT);
        navigLayer.setLeftWidth(0, Unit.EM, 15, Unit.EM);

        if (consoleVisible) {
            contentLayer.setLeftRight(15, Unit.EM, 40, Unit.EM);

            consoleLayer.setTopHeight(0.0, Unit.PCT, 100.0, Unit.PCT);
            consoleLayer.setRightWidth(0, Unit.EM, 40, Unit.EM);
            consoleLayer.setVisible(true);
        } else {
            contentLayer.setLeftRight(15, Unit.EM, 0, Unit.EM);

            consoleLayer.setVisible(false);
        }

        contentLayer.setTopHeight(0.0, Unit.PCT, 100.0, Unit.PCT);

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

    public void setConsoleVisible(boolean visible) {
        this.consoleVisible = visible;
        forceLayout();
    }

}