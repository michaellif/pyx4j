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
 * Created on 2011-04-14
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.dashboard.client;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

class TwoGadgetsRowFlowPanel extends FlowPanel {

    private final ColumnFlowPanel columnPanel;

    private final SimplePanel leftPlace = new SimplePanel();

    private final DropController leftDP = new TwoGadgetsRowDropController(leftPlace);

    private final SimplePanel rightPlace = new SimplePanel();

    private final DropController rightDP = new TwoGadgetsRowDropController(rightPlace);

    public TwoGadgetsRowFlowPanel(ColumnFlowPanel columnPanel) {
        super();
        this.columnPanel = columnPanel;

        // create two gadget place-holders: 

        this.columnPanel.dragController.registerDropController(leftDP);
        leftPlace.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        leftPlace.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        leftPlace.getElement().getStyle().setMarginRight(this.columnPanel.layout.getHorizontalSpacing() / 2, Unit.PCT);
        leftPlace.getElement().getStyle().setProperty("minHeight", "50px");
        leftPlace.setWidth(50.0 - this.columnPanel.layout.getHorizontalSpacing() / 2 - 0.1 + "%");
        this.add(leftPlace);

        this.columnPanel.dragController.registerDropController(rightDP);
        rightPlace.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        rightPlace.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        rightPlace.getElement().getStyle().setMarginLeft(this.columnPanel.layout.getHorizontalSpacing() / 2, Unit.PCT);
        rightPlace.getElement().getStyle().setProperty("minHeight", "50px");
        rightPlace.setWidth(50.0 - this.columnPanel.layout.getHorizontalSpacing() / 2 - 0.1 + "%");
        this.add(rightPlace);

        // style the row:   
        this.getElement().getStyle().setMarginTop(this.columnPanel.layout.getVerticalSpacing(), Unit.PX);
        this.getElement().getStyle().setMarginBottom(this.columnPanel.layout.getVerticalSpacing(), Unit.PX);
        this.setWidth("auto");
    }

    public boolean addToFreePlace(Widget w) {
        if (!isLeft()) {
            addLeft(w);
            return true;
        } else if (!isRight()) {
            addRight(w);
            return true;
        }
        return false;
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        System.out.println(">> insert for index = " + beforeIndex);
        switch (beforeIndex) {
        case 0:
            addLeft(w);
            break;
        case 1:
            if (isLeft()) {
                addRight(w);
            } else {
                addLeft(w);
            }
            break;
        case 2:
            addRight(w);
            break;
        }
    }

    @Override
    public Widget getWidget(int index) {
        System.out.println(">> getWidget for index = " + index);
        Widget w = null;
        switch (index) {
        case 0:
            w = (isLeft() ? leftPlace.getWidget() : rightPlace.getWidget());
            break;
        case 1:
            w = rightPlace.getWidget();
            break;
        }
        return w;
    }

    @Override
    public int getWidgetIndex(Widget child) {
        if (isLeft() && leftPlace.getWidget().equals(child)) {
            return 0;
        }
        if (isRight() && rightPlace.getWidget().equals(child)) {
            return 1;
        }
        return -1;
    }

    @Override
    public int getWidgetCount() {
        int count = 0;
        if (isLeft()) {
            ++count;
        }
        if (isRight()) {
            ++count;
        }
        return count;
    }

    @Override
    public boolean remove(Widget w) {
        boolean rv = super.remove(w);
        if (rv) {
            recalcHeight();
        }
        return rv;
    }

    public void addLeft(Widget w) {
        leftPlace.setWidget(w);
        recalcHeight();
        System.out.println(">> addLeft");
    }

    public void addRight(Widget w) {
        rightPlace.setWidget(w);
        recalcHeight();
        System.out.println(">> addRight");
    }

    public boolean isLeft() {
        return (leftPlace.getWidget() != null);
//          return (placeLeft.getWidget() != null && !(placeLeft.getWidget().getStyleName().equals(DashboardPanel.BASE_NAME
//          + DashboardPanel.StyleSuffix.DndRowPositioner)));
    }

    public boolean isRight() {
        return (rightPlace.getWidget() != null);
//          return (placeRight.getWidget() != null && !(placeRight.getWidget().getStyleName().equals(DashboardPanel.BASE_NAME
//          + DashboardPanel.StyleSuffix.DndRowPositioner)));
    }

    public boolean isFull() {
        return (isLeft() && isRight());
    }

    public boolean isEmpty() {
        return (!isLeft() && !isRight());
    }

    public boolean selfRemoveIfEmpty() {
        if (isEmpty()) {
            this.columnPanel.dragController.unregisterDropController(leftDP);
            this.columnPanel.dragController.unregisterDropController(rightDP);
            this.removeFromParent();
            System.out.println(">> selfClean() - cleaned!");
            return true;
        }
        return false;
    }

    public void recalcHeight() {
        int maxHeight = Math.max((isLeft() ? leftPlace.getWidget().getOffsetHeight() : 0), (isRight() ? rightPlace.getWidget().getOffsetHeight() : 0));
        if (maxHeight > 0) {
            leftPlace.getElement().getStyle().setHeight(maxHeight, Unit.PX);
            rightPlace.getElement().getStyle().setHeight(maxHeight, Unit.PX);
        }
    }
}