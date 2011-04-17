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
 * Created on 2011-04-13
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.dashboard.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.SimplePanel;


/**
 * DropController which allows a widget to be dropped on a SimplePanel drop target when
 * the drop target does not yet have a child widget.
 */
public class TwoGadgetsRowDropController extends SimpleDropController {

    private final SimplePanel dropTarget;

    public TwoGadgetsRowDropController(SimplePanel dropTarget) {
        super(dropTarget);
        this.dropTarget = dropTarget;
    }

    @Override
    public void onEnter(DragContext context) {
        if (context.draggable instanceof GadgetHolder && !((GadgetHolder) context.draggable).isFullWidth()) {
            if (dropTarget.getWidget() == null || dropTarget.getWidget().equals(context.draggable)) {
                dropTarget.addStyleName(DashboardPanel.BASE_NAME + DashboardPanel.StyleSuffix.DndRowPositioner);
//            dropTarget.setHeight(context.draggable.getOffsetHeight() - DOMUtil.getVerticalBorders(dropTarget) + "px");
//            dropTarget.setWidth(context.draggable.getOffsetWidth() - DOMUtil.getHorizontalBorders(dropTarget) + "px");
            }
        }
    }

    @Override
    public void onLeave(DragContext context) {
        dropTarget.removeStyleName(DashboardPanel.BASE_NAME + DashboardPanel.StyleSuffix.DndRowPositioner);
//        dropTarget.setHeight("auto");
    }

    @Override
    public void onDrop(DragContext context) {
        dropTarget.setWidget(context.draggable);
        super.onDrop(context);
    }

    @Override
    public void onPreviewDrop(DragContext context) throws VetoDragException {
        if (dropTarget.getWidget() != null) {
            throw new VetoDragException();
        }

        // do not allow full width gadget drops here:
        if (context.draggable instanceof GadgetHolder && ((GadgetHolder) context.draggable).isFullWidth()) {
            throw new VetoDragException();
        }

        super.onPreviewDrop(context);
    }
}
