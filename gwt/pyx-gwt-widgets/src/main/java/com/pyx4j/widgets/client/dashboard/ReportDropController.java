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
 * Created on Apr 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.allen_sauer.gwt.dnd.client.util.Area;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.dashboard.ReportLayoutPanel.CellPanel;

public class ReportDropController extends AbstractPositioningDropController {

    protected final ReportLayoutPanel dropTarget;

    private int dropIndex;

    private ReportGadgetPositioner positioner = null;

    public static final LocationWidgetComparator FULL_COMPARATOR = new LocationWidgetComparator() {

        @Override
        public boolean locationIndicatesIndexFollowingWidget(Area widgetArea, Location location) {
            return false;
        }
    };

    public ReportDropController(ReportLayoutPanel dropTarget) {
        super(dropTarget);
        this.dropTarget = dropTarget;
    }

    @Override
    public void onDrop(DragContext context) {
        if (dropIndex >= -1 && dropIndex <= dropTarget.getWidgetCount() && context.selectedWidgets.size() == 1) {
            dropTarget.setGadget(context.selectedWidgets.get(0), dropIndex);
        } else {
            throw new Error("Only single Gadget can be selected");
        }
        super.onDrop(context);
    }

    @Override
    public void onEnter(DragContext context) {
        super.onEnter(context);
        if (context.selectedWidgets.size() == 1) {
            positioner = newPositioner(context);
            int index = dropTarget.getGadgetIndex(context.mouseX, context.mouseY);
            dropTarget.setGadget(positioner, index);
        } else {
            throw new Error("Only single Gadget can be selected");
        }
    }

    @Override
    public void onLeave(DragContext context) {
        positioner = null;
        super.onLeave(context);
    }

    @Override
    public void onMove(DragContext context) {
        super.onMove(context);

        int targetIndex = calculateInsertionIndex(context.mouseX, context.mouseY);
        int positionerIndex = dropTarget.getGadgetIndex(positioner);

        if (positionerIndex > -1 && targetIndex > -1 && targetIndex != positionerIndex) {

            Report.Location location = dropTarget.getGadgetLocation(positioner);
            if (!Report.Location.Full.equals(location)) {
                location = calculateInsertionLocation(context.mouseX);
            }
            HTML keeper = new HTML();
            dropTarget.setGadget(keeper, positionerIndex);
            dropTarget.insertGadget(positioner, location, targetIndex);
            dropTarget.removeGadget(keeper);
        }
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        dropIndex = dropTarget.getGadgetIndex(positioner);
        super.onPreviewDrop(context);
    }

    protected ReportGadgetPositioner newPositioner(DragContext context) {
        int height = 0;
        if (context.selectedWidgets.size() == 1) {
            Widget widget = context.selectedWidgets.get(0);
            height = widget.getOffsetHeight();
        } else {
            throw new Error("Single Gadget can be selected");
        }

        return new ReportGadgetPositioner(height);
    }

    public int calculateInsertionIndex(int mouseX, int mouseY) {
        int topCellIndex = -1;
        int bottomCellIndex = -1;
        for (int i = 0; i < dropTarget.getWidgetCount(); i++) {
            CellPanel cellPanel = (CellPanel) dropTarget.getWidget(i);
            if (bottomCellIndex == -1 && cellPanel.getAbsoluteTop() > mouseY) {
                bottomCellIndex = i;
                break;
            } else {
                topCellIndex = i;
            }
        }

        int cellIndex = topCellIndex;
        CellPanel cellPanel = (CellPanel) dropTarget.getWidget(topCellIndex);
        if (!Report.Location.Full.equals(cellPanel.getLocation())) {
            CellPanel leftCellPanel = (CellPanel) dropTarget.getWidget(topCellIndex - 1);
            if (leftCellPanel.getAbsoluteLeft() <= mouseX && mouseX <= (leftCellPanel.getAbsoluteLeft() + leftCellPanel.getOffsetWidth())) {
                cellIndex = topCellIndex - 1;
                cellPanel = leftCellPanel;
            }
        }

        if (cellPanel.isSpaceHolder() || cellPanel.isPositioner()) {

        } else if (mouseY > (cellPanel.getAbsoluteTop() + cellPanel.getOffsetHeight() / 2)) {
            if (bottomCellIndex == -1) {
                cellIndex = dropTarget.getWidgetCount();
            } else {
                CellPanel bottomCellPanel = (CellPanel) dropTarget.getWidget(bottomCellIndex);
                if (Report.Location.Right.equals(bottomCellPanel.getLocation())) {
                    cellIndex = bottomCellIndex + 1;
                } else {
                    cellIndex = bottomCellIndex;
                }
            }
        }

        return cellIndex;
    }

    public Report.Location calculateInsertionLocation(int mouseX) {
        return (mouseX > dropTarget.getOffsetWidth() / 2) ? Report.Location.Right : Report.Location.Left;
    }
}