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
 * Created on 2011-05-01
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import java.util.NoSuchElementException;
import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.dashboard.Dashboard.Layout;

class DashboardLayoutPanel extends FlowPanel implements DashboardEvent {

    protected Layout layoutType = Layout.Three;

    private final PickupDragController gadgetDragController;

    private final boolean isRefreshAllowed = true;

    private final DashboardEvent handler;

    public DashboardLayoutPanel(PickupDragController gadgetDragController, DashboardEvent handler) {
        this.gadgetDragController = gadgetDragController;
        this.handler = handler;

        getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        setWidth("100%");
        initColumns();
    }

    // Layout manipulation:
    public Layout getLayout() {
        return this.layoutType;
    }

    public boolean setLayout(Layout layout) {
        if (!isRefreshAllowed()) {
            return false;
        }

        this.layoutType = layout; // accept new layout
        return refresh();
    }

    // Widget manipulation: 
    public boolean addGadget(Widget widget) {
        return insertGadget(widget, 0, 0);
    }

    public boolean addGadget(Widget widget, int column) {
        return insertGadget(widget, column, -1);
    }

    public boolean addGadget(Widget widget, int column, int row) {
        return insertGadget(widget, column, row);
    }

    public boolean insertGadget(Widget widget, int column, int row) {
        if (checkIndexes(column, row, true)) {
            if (row >= 0) {
                getColumnPanel(column).insert(widget, row);
            } else {
                // if row is negative - just add at the end:
                getColumnPanel(column).add(widget);
            }
            onEvent(Reason.addGadget);
            return true;
        }
        return false;
    }

    public static class Location {
        public int col;

        public int row;
    }

    public boolean getWidgetLocation(Widget widget, Location loc) {

        for (int i = 0; i < getColumnsCount(); ++i) {
            for (int j = 0; j < getColumnPanel(i).getWidgetCount(); ++j) {
                if (widget.equals(getColumnPanel(i).getWidget(j))) {
                    loc.col = i;
                    loc.row = j;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean removeGadget(int column, int row) {
        return (checkIndexes(column, row, false) && getColumnPanel(column).remove(row));
    }

    @Override
    public void clear() {
        gadgetDragController.unregisterDropControllers();
        super.clear();
    }

    private boolean refresh() {
        if (!isRefreshAllowed()) {
            return false;
        }

        int gadgetsCount = 0;
        // hold the current widgets for a while:
        Vector<DashboardColumnFlowPanel> columnPanels = new Vector<DashboardColumnFlowPanel>(getWidgetCount());
        for (int i = 0; i < getWidgetCount(); ++i) {
            DashboardColumnFlowPanel cp = getColumnPanel(i);
            gadgetsCount += cp.getWidgetCount();
            columnPanels.add(cp);
        }

        initColumns(); // initialize new columns according to the (new) layout

        // if new columns count the same as previous one - just move gadgets one to one:
        if (getWidgetCount() == columnPanels.size()) {
            for (int i = 0; i < columnPanels.size(); ++i) {
                while (columnPanels.get(i).getWidgetCount() > 0) {
                    getColumnPanel(i).add(columnPanels.get(i).getWidget(0));
                }
            }
        } else { // 'equalize' gadgets per columns:
            Vector<Widget> allGadgets = new Vector<Widget>(gadgetsCount);
            for (int i = 0; i < columnPanels.size(); ++i) {
                for (int j = 0; j < columnPanels.get(i).getWidgetCount(); ++j) {
                    allGadgets.add(columnPanels.get(i).getWidget(j));
                }
            }

            int gadgetsPerColumn = gadgetsCount / getWidgetCount();
            for (int i = 0; i < getWidgetCount(); ++i) {
                int size = (i == getWidgetCount() - 1 ? allGadgets.size() : Math.min(gadgetsPerColumn, allGadgets.size()));
                for (int j = 0; j < size; ++j) {
                    getColumnPanel(i).add(allGadgets.firstElement());
                    allGadgets.remove(0);
                }
            }
        }

        onEvent(Reason.newLayout);
        return true;
    }

    public boolean isRefreshAllowed() {
        return isRefreshAllowed;
    }

    protected void initColumns() {
        clear();

        for (int col = 0; col < layoutType.columns(); ++col) {
            // vertical panel to hold the heading and a second vertical panel for widgets:
            DashboardColumnFlowPanel columnPanel = new DashboardColumnFlowPanel(this);
            columnPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            columnPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);

            switch (layoutType) {
            case Two12:
                columnPanel.setWidth(100.0 / 3 * (col == 0 ? 1 : 2) + "%");
                break;

            case Two21:
                columnPanel.setWidth(100.0 / 3 * (col == 0 ? 2 : 1) + "%");
                break;

            default:
                columnPanel.setWidth(100.0 / layoutType.columns() + "%");
            }

            // widget drop controller for the current column:
            gadgetDragController.registerDropController(new DashboardDropController(columnPanel));
            add(columnPanel);
        }
    }

    protected DashboardColumnFlowPanel getColumnPanel(int column) {
        return (DashboardColumnFlowPanel) getWidget(column);
    }

    protected int getColumnsCount() {
        return getWidgetCount();
    }

    protected boolean checkIndexes(int column, int row, boolean insert) {
        if (column < 0 || column >= getWidgetCount()) {
            return false;
        }

        if (insert) {
            if (row > getColumnPanel(column).getWidgetCount()) {
                return false;
            }
        } else if (row < 0 || row >= getColumnPanel(column).getWidgetCount()) {
            return false;
        }

        return true;
    }

    @Override
    public void onEvent(Reason reason) {
        handler.onEvent(reason);
    }

    // Iteration stuff:
    private class GadgetIterator implements IGadgetIterator {

        private int col = 0;

        private int row = -1;

        @Override
        public boolean hasNext() {
            return ((col + 1) < getColumnsCount() || (row + 1) < getColumnPanel(col).getWidgetCount());
        }

        @Override
        public IGadget next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if ((row + 1) < getColumnPanel(col).getWidgetCount()) {
                return ((GadgetHolder) getColumnPanel(col).getWidget(++row)).getGadget(); // current column...
            } else {
                return ((GadgetHolder) getColumnPanel(++col).getWidget(row = 0)).getGadget(); // next column...
            }
        }

        @Override
        public void remove() {
            if (!checkIndexes(col, row, false)) {
                throw new NoSuchElementException();
            }
            removeGadget(col, row--);
        }

        @Override
        public int getColumn() {
            return col;
        }
    }

    public IGadgetIterator getGadgetIterator() {
        return new GadgetIterator();
    }
}