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
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.dashboard.BoardEvent.Reason;

class DashboardDropController extends FlowPanelDropController {

    /**
     * @param dropTarget
     */
    public DashboardDropController(FlowPanel dropTarget) {
        super(dropTarget);
    }

    @Override
    protected LocationWidgetComparator getLocationWidgetComparator() {
        return LocationWidgetComparator.BOTTOM_HALF_COMPARATOR;
    }

    @Override
    protected Widget newPositioner(DragContext context) {
        int width = 0;
        int height = 0;
        if (context.selectedWidgets.size() == 1) {
            Widget widget = context.selectedWidgets.get(0);
            width = Math.max(width, widget.getOffsetWidth());
            height = widget.getOffsetHeight();
        } else {
            throw new Error("Only single Gadget can be selected");
        }

        return new DashboardGadgetPositioner(width, height);
    }

    @Override
    public void onDrop(DragContext context) {
        if (context.selectedWidgets.size() > 1) {
            throw new Error("Only single Gadget can be selected");
        }

        super.onDrop(context);

        // notify board:
        if (getDropTarget() instanceof BoardEvent) {
            ((BoardEvent) getDropTarget()).onEvent(Reason.repositionGadget);
        }
    }
}