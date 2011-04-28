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
            dropTarget.setGadget(context.selectedWidgets.get(0), positioner.isFullWidth(), dropIndex);
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
            dropTarget.setGadget(positioner, isFullWidthGadget(context.selectedWidgets.get(0)), index);
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

        int targetIndex = dropTarget.getInsertionIndex(context.mouseX, context.mouseY);
        int positionerIndex = dropTarget.getGadgetIndex(positioner);

        System.out.println("getInsertionIndex+++++++++++++++ " + targetIndex);
        System.out.println("getGadgetIndex----- " + positionerIndex);

        if (targetIndex > -1 && targetIndex != positionerIndex) {
            dropTarget.removeGadget(positioner);
            dropTarget.insertGadget(positioner, positioner.isFullWidth(), targetIndex);
        }
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        dropIndex = dropTarget.getGadgetIndex(positioner);
        System.out.println("onPreviewDrop+++++++++++++++ " + dropIndex);
        super.onPreviewDrop(context);
    }

    protected ReportGadgetPositioner newPositioner(DragContext context) {
        boolean fullWidth = false;
        int height = 0;
        if (context.selectedWidgets.size() == 1) {
            Widget widget = context.selectedWidgets.get(0);
            fullWidth = isFullWidthGadget(widget);
            height = widget.getOffsetHeight();
        } else {
            throw new Error("Single Gadget can be selected");
        }

        return new ReportGadgetPositioner(fullWidth, height);
    }

    boolean isFullWidthGadget(Widget widget) {
        return widget.getOffsetWidth() > dropTarget.getOffsetWidth() * 2 / 3;
    }

}