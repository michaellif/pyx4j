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
        System.out.println("onDrop+++++++++++++++ " + dropIndex);
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

        System.out.println("targetIndex+++++++++++++++ " + targetIndex);
        //System.out.println("positionerIndex----- " + positionerIndex);

        if (targetIndex > -1 && targetIndex != positionerIndex) {
            Widget beforeGadget = dropTarget.getGadget(targetIndex);
            ReportLayoutPanel.Location location = dropTarget.getGadgetLocation(positioner);
            if (!ReportLayoutPanel.Location.Full.equals(location)) {
                location = calculateInsertionLocation(context.mouseX);
            }
//            dropTarget.removeGadget(positioner);
//            dropTarget.insertGadget(positioner, location, dropTarget.getGadgetIndex(beforeGadget));
        }
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        dropIndex = dropTarget.getGadgetIndex(positioner);
        System.out.println("onPreviewDrop+++++++++++++++ " + dropIndex);
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

        System.out.println(")))))))))))))" + topCellIndex + " " + bottomCellIndex);

        int cellIndex = -1;
        CellPanel cellPanel = null;
        for (int i = topCellIndex - 1; i <= topCellIndex; i++) {
            if (i > -1 && i < dropTarget.getWidgetCount()) {
                cellPanel = (CellPanel) dropTarget.getWidget(i);
                if (cellPanel.getAbsoluteLeft() <= mouseX && mouseX <= (cellPanel.getAbsoluteLeft() + cellPanel.getOffsetWidth())) {
                    cellIndex = i;
                    break;
                }
            }
        }

        System.out.println(")))))))))))))" + cellIndex);

        return cellIndex;
    }

    public ReportLayoutPanel.Location calculateInsertionLocation(int mouseX) {
        return (mouseX > dropTarget.getOffsetWidth() / 2) ? ReportLayoutPanel.Location.Right : ReportLayoutPanel.Location.Left;
    }
}