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

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.DashboardPanel2.GadgetHolder;

/**
 * {@link VerticalPanel} which has a permanent spacer at the end to prevent CSS collapse
 * of the panel and its parent.
 */
public class ColumnFlowPanel extends FlowPanel /* VerticalPanel */{

    final PickupDragController dragController;

    private boolean rowGadgetIsDragging = false;

    protected Layout layout;

    public ColumnFlowPanel() {
        this.dragController = null;
        clear();
    }

    public ColumnFlowPanel(PickupDragController dragCtrl, Layout layout) {
        this.dragController = dragCtrl;
        this.dragController.addDragHandler(new DragHandler() {

            @Override
            public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
            }

            @Override
            public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
            }

            @Override
            public void onDragStart(DragStartEvent event) {
                if (event.getContext().draggable instanceof GadgetHolder) {
                    if (!((GadgetHolder) event.getContext().draggable).isFullWidth()) {
                        setRowGadgetIsDragging(true);
                        recalcHeight();
                    }
                }
            }

            @Override
            public void onDragEnd(DragEndEvent event) {
                if (isRowGadgetIsDragging()) {
                    setRowGadgetIsDragging(false);
                    clearEmptyRows();
                    recalcHeight();
                }
            }
        });

        this.layout = layout;
        clear();
    }

    @Override
    public void add(Widget w) {
        insert(w, getWidgetCount());
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        if (beforeIndex == super.getWidgetCount()) {
            --beforeIndex;
        }

        System.out.println(">> VerticalPanelWithSpacer.insert() with beforeIndex = " + beforeIndex);
        System.out.println("> Widget ClassName is " + w.getClass());
        System.out.println("> Widget StyleName is " + w.getStyleName());

        boolean needNewRow = false;
        if (w instanceof GadgetHolder) {
            needNewRow = !((GadgetHolder) w).isFullWidth();
        } else if (isRowGadgetIsDragging()) {
            needNewRow = (w.getStyleName().equals(DashboardPanel.BASE_NAME + DashboardPanel.StyleSuffix.DndPositioner));
//        } else {
//            needRow = (w.getStyleName().equals(DashboardPanelHorizontal.BASE_NAME + DashboardPanelHorizontal.StyleSuffix.DndPositioner));
        }

        if (needNewRow) {
            System.out.println("> new Row going to be inserted !?.");
        }

        // try existing :
        if (needNewRow) {
            Widget row = super.getWidget(beforeIndex > 0 ? beforeIndex - 1 : beforeIndex);
            if (row.getClass().equals(TwoGadgetsRowFlowPanel.class)) {
                TwoGadgetsRowFlowPanel subRow = (TwoGadgetsRowFlowPanel) row;
                if (subRow.addToFreePlace(w)) {
                    return;
                }
            }
            if (isRowGadgetIsDragging()) {
                row = super.getWidget(beforeIndex);
                if (row.getClass().equals(TwoGadgetsRowFlowPanel.class)) {
                    TwoGadgetsRowFlowPanel subRow = (TwoGadgetsRowFlowPanel) row;
                    if (subRow.addToFreePlace(w)) {
                        return;
                    }
                }
            }
        }

        super.insert(needNewRow ? createRow(w) : w, beforeIndex);
    }

    @Override
    public int getWidgetCount() {
        return (super.getWidgetCount() - 1);
    }

    @Override
    public void clear() {
        super.clear();
        Label spacerLabel = new Label("");
        spacerLabel.setStylePrimaryName(DashboardPanel.BASE_NAME + DashboardPanel.StyleSuffix.ColumnSpacer);
        super.add(spacerLabel);
    }

    // internals:
    private Widget createRow(Widget w) {
        if (dragController != null) {
            TwoGadgetsRowFlowPanel r = new TwoGadgetsRowFlowPanel(this);
            r.addLeft(w);
            System.out.println(">> createRow() - done");
            return r;
        }
        return w;
    }

    protected void clearEmptyRows() {
        boolean cleaned = false;
        for (Widget w : this) {
            if (w.getClass().equals(TwoGadgetsRowFlowPanel.class)) {
                cleaned = (cleaned || ((TwoGadgetsRowFlowPanel) w).selfRemoveIfEmpty());
            }
        }
        // it seems that removing object in the loop
        // messes iteration!!! So repeat one more:  
        if (cleaned) {
            clearEmptyRows();
        }
    }

    protected void recalcHeight() {
        for (Widget w : this) {
            if (w.getClass().equals(TwoGadgetsRowFlowPanel.class)) {
                ((TwoGadgetsRowFlowPanel) w).recalcHeight();
            }
        }
    }

    protected boolean isRowGadgetIsDragging() {
        return rowGadgetIsDragging;
    }

    private void setRowGadgetIsDragging(boolean rowGadgetIsDragging) {
        this.rowGadgetIsDragging = rowGadgetIsDragging;
    }
}
