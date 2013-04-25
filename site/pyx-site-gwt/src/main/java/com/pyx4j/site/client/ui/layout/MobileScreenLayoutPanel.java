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

public class MobileScreenLayoutPanel extends ComplexPanel implements RequiresResize, ProvidesResize {

    private final Layout layout;

    private final DisplayPanel headerDisplay;

    private final DisplayPanel listerDisplay;

    private final DisplayPanel detailsDisplay;

    private PageOrientation pageOrientation = PageOrientation.Vertical;

    private boolean listerLayout = false;

    public MobileScreenLayoutPanel() {
        setElement(Document.get().createDivElement());

        layout = new Layout(getElement());

        // ============ Header ============
        {
            headerDisplay = new DisplayPanel();
            Layer layer = layout.attachChild(headerDisplay.asWidget().getElement(), headerDisplay);
            headerDisplay.setLayoutData(layer);

            getChildren().add(headerDisplay);
            adopt(headerDisplay);
        }

        // ============ Lister ============
        {
            listerDisplay = new DisplayPanel();
            listerDisplay.setStyleName(MobileScreenLayoutPanelTheme.StyleName.Lister.name());
            Layer layer = layout.attachChild(listerDisplay.asWidget().getElement(), listerDisplay);
            listerDisplay.setLayoutData(layer);

            getChildren().add(listerDisplay);
            adopt(listerDisplay);
        }

        // ============ Details ============
        {
            detailsDisplay = new DisplayPanel();
            detailsDisplay.setStyleName(MobileScreenLayoutPanelTheme.StyleName.Details.name());
            Layer layer = layout.attachChild(detailsDisplay.asWidget().getElement(), detailsDisplay);
            detailsDisplay.setLayoutData(layer);

            getChildren().add(detailsDisplay);
            adopt(detailsDisplay);
        }

        forceLayout();
    }

    public DisplayPanel getHeaderDisplay() {
        return headerDisplay;
    }

    public DisplayPanel getListerDisplay() {
        return listerDisplay;
    }

    public DisplayPanel getDetailsDisplay() {
        return detailsDisplay;
    }

    public void forceLayout() {
        doLayout();
        layout.layout();
        onResize();
    }

    private void doLayout() {

        Layer headerLayer = (Layer) headerDisplay.getLayoutData();
        Layer listerLayer = (Layer) listerDisplay.getLayoutData();
        Layer detailsLayer = (Layer) detailsDisplay.getLayoutData();

        headerLayer.setTopHeight(0.0, Unit.PCT, 10.0, Unit.PCT);

        double width = showDetailsDisplay() ? 50.0 : 100.0;

        listerLayer.setTopBottom(10.0, Unit.PCT, 0.0, Unit.PCT);
        listerLayer.setLeftWidth(0.0, Unit.PCT, width, Unit.PCT);

        detailsLayer.setTopBottom(10.0, Unit.PCT, 0.0, Unit.PCT);
        detailsLayer.setRightWidth(0.0, Unit.PCT, width, Unit.PCT);
        detailsDisplay.setVisible(showDetailsDisplay());
    }

    private boolean showDetailsDisplay() {
        return !listerLayout && PageOrientation.Horizontal == pageOrientation;
    }

    @Override
    public void onResize() {
        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }

    public void setPageOrientation(PageOrientation pageOrientation) {
        this.pageOrientation = pageOrientation;
        forceLayout();
    }

    public void setListerLayout(boolean listerLayout) {
        this.listerLayout = listerLayout;
        forceLayout();
    }

}
