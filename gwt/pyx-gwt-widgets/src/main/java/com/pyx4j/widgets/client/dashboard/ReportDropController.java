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

import com.pyx4j.widgets.client.dashboard.ReportLayoutPanel.CellCoordinates;

public class ReportDropController extends AbstractPositioningDropController {

    protected final ReportLayoutPanel dropTarget;

    private CellCoordinates dropCoordinates;

    private Widget positioner = null;

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
        if (dropCoordinates != null && context.selectedWidgets.size() == 1) {
            dropTarget.setGadget(context.selectedWidgets.get(0), dropCoordinates.getRow(), dropCoordinates.getColumn());
        } else {
            throw new Error("Single Gadget can be selected");
        }
        super.onDrop(context);
    }

    @Override
    public void onEnter(DragContext context) {
        super.onEnter(context);
        positioner = newPositioner(context);
        CellCoordinates targetLocation = dropTarget.getGadgetLocation(context.mouseX, context.mouseY);
        dropTarget.setGadget(positioner, targetLocation.getRow(), targetLocation.getColumn());
    }

    @Override
    public void onLeave(DragContext context) {
        positioner = null;
        super.onLeave(context);
    }

    @Override
    public void onMove(DragContext context) {
        super.onMove(context);

        CellCoordinates targetLocation = dropTarget.getGadgetLocation(context.mouseX, context.mouseY);
        CellCoordinates positionerLocation = dropTarget.getGadgetLocation(positioner);

        System.out.println("onMove+++++++++++++++ " + targetLocation);
        System.out.println("onMove----- " + positionerLocation);

        if (targetLocation != null && !targetLocation.equals(positionerLocation)) {
            dropTarget.insertGadget(positioner, targetLocation.getRow(), targetLocation.getColumn());
        }

        //        if (positionerRowIndex != targetRowIndex && (positionerRowIndex != targetRowIndex - 1 || targetRowIndex == 0)) {
        //            if (positionerRowIndex == 0 && dropTarget.getWidgetCount() == 1) {
        //                // do nothing, the positioner is the only widget
        //            } else if (targetRowIndex == -1) {
        //                // outside drop target, so remove positioner to indicate a drop will not happen
        //                dropTarget.removeGadget(positioner);
        //            } else {
        // dropTarget.insertGadget(positioner, targetRowIndex, targetColumnIndex);
        //            }
        //        }
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        dropCoordinates = dropTarget.getGadgetLocation(positioner);
        super.onPreviewDrop(context);
    }

    protected Widget newPositioner(DragContext context) {
        int width = 0;
        int height = 0;
        if (context.selectedWidgets.size() == 1) {
            Widget widget = context.selectedWidgets.get(0);
            width = Math.max(width, widget.getOffsetWidth());
            height = widget.getOffsetHeight();
        } else {
            throw new Error("Single Gadget can be selected");
        }

        return new GadgetPositioner(width, height);
    }

}