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
 * Created on 2011-02-14
 * @author VladLL
 * @version $Id$
 */
package com.pyx4j.dashboard.client;

import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.images.DashboardImages;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleName;

/**
 * Dashboard panel.
 */
public class DashboardPanel extends SimplePanel {

    // CSS style names: 
    public static String BASE_NAME = "pyx4j_DashboardPanel";

    public static enum StyleSuffix implements IStyleName {
        Column, ColumnHeading, ColumnSpacer, Holder, HolderSetup, HolderCaption, HolderHeading, HolderMenu, DndPositioner, DndRowPositioner
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, selected, hover
    }

    // resources:
    protected DashboardImages images = (DashboardImages) GWT.create(DashboardImages.class);

    // internal data:	
    protected Layout layout;

    protected PickupDragController widgetDragController;

    protected FlowPanel columnsContainerPanel; // holds columns (as vertical panels).

    private boolean isRefreshAllowed;

    // construction:
    public DashboardPanel() {
        this.layout = new Layout();
        init();
    }

    public DashboardPanel(Layout layout) {
        this.layout = layout;
        init();
    }

    // Layout manipulation:
    public Layout getLayout() {
        return this.layout;
    }

    public boolean setLayout(Layout layout) {
        if (!isRefreshAllowed()) {
            return false;
        }

        this.layout = layout; // accept new layout
        return refresh();
    }

    // Widget manipulation:	
    public boolean addGadget(IGadget widget) {
        return insertGadget(widget, 0, 0);
    }

    public boolean addGadget(IGadget widget, int column) {
        return insertGadget(widget, column, -1);
    }

    public boolean insertGadget(IGadget widget, int column, int row) {
        if (checkIndexes(column, row, true)) {
            // create holder for supplied widget and insert it into specified column,row:
            GadgetHolder gh = new GadgetHolder(widget, this);

            if (row >= 0) {
                getColumnWidgetsPanel(column).insert(gh, row);
            } else {
                // if row is negative - just add at the end:
                getColumnWidgetsPanel(column).add(gh);
            }

            return true;
        }

        return false;
    }

    public boolean removeGadget(int column, int row) {
        return (checkIndexes(column, row, false) && getColumnWidgetsPanel(column).remove(row));
    }

    public void removeAllGadgets() {
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
            getColumnWidgetsPanel(i).clear();
        }
    }

    @Override
    public void clear() {
        widgetDragController.unregisterDropControllers();
        columnsContainerPanel.clear();
    }

    public boolean refresh() {
        if (!isRefreshAllowed()) {
            return false;
        }

        int gadgetsCount = 0;
        // hold the current widgets for a while:
        Vector<ColumnFlowPanel> columnWidgetsPanels = new Vector<ColumnFlowPanel>(columnsContainerPanel.getWidgetCount());
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
            ColumnFlowPanel cwp = getColumnWidgetsPanel(i);
            gadgetsCount += cwp.getWidgetCount();
            columnWidgetsPanels.add(cwp);
        }

        initColumns(); // initialize new columns according to the (new) layout

        // if new columns count the same as previous one - just move gadgets one to one:
        if (columnsContainerPanel.getWidgetCount() == columnWidgetsPanels.size()) {
            for (int i = 0; i < columnWidgetsPanels.size(); ++i) {
                while (columnWidgetsPanels.get(i).getWidgetCount() > 0) {
                    getColumnWidgetsPanel(i).add(columnWidgetsPanels.get(i).getWidget(0));
                }
            }
        } else { // 'equalize' gadgets per columns:
            Vector<Widget> allGadgets = new Vector<Widget>(gadgetsCount);
            for (int i = 0; i < columnWidgetsPanels.size(); ++i) {
                for (int j = 0; j < columnWidgetsPanels.get(i).getWidgetCount(); ++j) {
                    allGadgets.add(columnWidgetsPanels.get(i).getWidget(j));
                }
            }

            int gadgetsPerColumn = gadgetsCount / columnsContainerPanel.getWidgetCount();
            for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
                int size = (i == columnsContainerPanel.getWidgetCount() - 1 ? allGadgets.size() : Math.min(gadgetsPerColumn, allGadgets.size()));
                for (int j = 0; j < size; ++j) {
                    getColumnWidgetsPanel(i).add(allGadgets.firstElement());
                    allGadgets.remove(0);
                }
            }
        }

        return true;
    }

    public boolean isRefreshAllowed() {
        return isRefreshAllowed;
    }

    public void setRefreshAllowed(boolean isRefreshAllowed) {
        this.isRefreshAllowed = isRefreshAllowed;
    }

    // initializing:
    protected void init() {
        addStyleName(BASE_NAME);
        setRefreshAllowed(true);

        // use the boundary panel as this composite's widget:
        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        // initialize horizontal panel to hold our columns:
        columnsContainerPanel = new FlowPanel();
        columnsContainerPanel.setWidth("100%");

        boundaryPanel.add(columnsContainerPanel);

        // initialize our widget drag controller:
        widgetDragController = new PickupDragController(boundaryPanel, false);
        widgetDragController.setBehaviorMultipleSelection(false);

        initColumns();
    }

    protected void initColumns() {
        clear();

        for (int col = 0; col < layout.getColumns(); ++col) {
            // vertical panel to hold the heading and a second vertical panel for widgets:
            SimplePanel columnCompositePanel = new SimplePanel();
            columnCompositePanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            columnCompositePanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            columnCompositePanel.getElement().getStyle().setMarginLeft(layout.getHorizontalSpacing(), Unit.PCT);
            columnCompositePanel
                    .setWidth(((layout.isColumnWidths() ? layout.getCoumnWidth(col) : 100.0 / layout.getColumns()) - layout.getHorizontalSpacing() - layout
                            .getHorizontalSpacing() / layout.getColumns())
                            + "%");

            // inner vertical panel to hold individual widgets:
            ColumnFlowPanel columnPanel = new ColumnFlowPanel(widgetDragController, layout);
            columnPanel.addStyleName(BASE_NAME + StyleSuffix.Column);
            columnPanel.getElement().getStyle().setProperty("WebkitBoxSizing", "border-box");
            columnPanel.getElement().getStyle().setProperty("MozBoxSizing", "border-box");
            columnPanel.getElement().getStyle().setProperty("boxSizing", "border-box");
            columnPanel.setWidth("100%");

            // widget drop controller for the current column:
            widgetDragController.registerDropController(new CustomFlowPanelDropController(columnPanel, layout));

            columnCompositePanel.add(columnPanel);
            columnsContainerPanel.add(columnCompositePanel);
        }
    }

    // internals:	
    protected ColumnFlowPanel getColumnWidgetsPanel(int column) {
        return (ColumnFlowPanel) ((SimplePanel) columnsContainerPanel.getWidget(column)).getWidget();
    }

    protected boolean checkIndexes(int column, int row, boolean insert) {
        if (column >= columnsContainerPanel.getWidgetCount()) {
            return false;
        }

        if (insert) {
            if (row > getColumnWidgetsPanel(column).getWidgetCount()) {
                return false;
            }
        } else if (row >= getColumnWidgetsPanel(column).getWidgetCount()) {
            return false;
        }

        return true;
    }
} // DashboardPanel class...
