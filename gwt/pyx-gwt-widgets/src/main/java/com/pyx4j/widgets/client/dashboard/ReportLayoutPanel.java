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
 * Created on Apr 19, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportLayoutPanel extends FlowPanel implements DashboardEvent {

    protected static final Logger log = LoggerFactory.getLogger(ReportLayoutPanel.class);

    private final DashboardEvent handler;

    public ReportLayoutPanel(DashboardEvent handler) {
        this.handler = handler;
        getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        setWidth("100%");
    }

    public void addGadget(Widget widget, Report.Location location) {
        insertGadget(widget, location, getWidgetCount());
    }

    public void insertGadget(Widget widget, Report.Location location, int beforeIndex) {
        if (!checkIndex(beforeIndex)) {
            return;
        }

        // check for empty cell in the index vicinity :

        CellPanel beforeCell = null;
        Report.Location beforeCellLocation = null;
        boolean isBeforeCellSpaceHolder = false;
        if (beforeIndex < getWidgetCount()) {
            beforeCell = (CellPanel) getWidget(beforeIndex);
            beforeCellLocation = beforeCell.getLocation();
            isBeforeCellSpaceHolder = beforeCell.isSpaceHolder();
        }

        if (isBeforeCellSpaceHolder && location.equals(beforeCellLocation)) {
            beforeCell.setWidget(widget);
            return; // found!..
        }

        CellPanel afterCell = null;
        Report.Location afterCellLocation = null;
        boolean isAfterCellSpaceHolder = false;
        if (beforeIndex > 0) {
            afterCell = (CellPanel) getWidget(beforeIndex - 1);
            afterCellLocation = afterCell.getLocation();
            isAfterCellSpaceHolder = afterCell.isSpaceHolder();
        }

        if (isAfterCellSpaceHolder && location.equals(afterCellLocation)) {
            afterCell.setWidget(widget);
            return; // found!..
        }

        // ok, from here - create new row:

        CellPanel cell = new CellPanel(location);
        cell.setWidget(widget);

        if (Report.Location.Right.equals(beforeCellLocation)) {
            beforeIndex = beforeIndex - 1;
        }

        switch (location) {
        case Left:
            insert(new CellPanel(Report.Location.Right), beforeIndex);
            insert(cell, beforeIndex);
            break;
        case Right:
            insert(cell, beforeIndex);
            insert(new CellPanel(Report.Location.Left), beforeIndex);
            break;
        case Full:
            insert(cell, beforeIndex);
            break;
        default:
            log.debug("Wrong beforeIndex - {}", beforeIndex);
            break;
        }
    }

    public void setGadget(Widget widget, int index) {
        if (!checkIndex(index)) {
            return;
        }

        CellPanel cell = (CellPanel) getWidget(index);
        cell.setWidget(widget);
    }

    public void removeGadget(int index) {
        if (!checkIndex(index)) {
            return;
        }

        CellPanel cell = (CellPanel) getWidget(index);
        if (Report.Location.Full.equals(cell.getLocation())) {
            remove(cell);
        } else if (Report.Location.Left.equals(cell.getLocation())) {
            if (index + 1 < getWidgetCount()) {
                CellPanel nextCell = (CellPanel) getWidget(index + 1);
                if (nextCell.isSpaceHolder()) {
                    remove(cell);
                    remove(nextCell);
                } else {
                    cell.setSpaceHolder();
                }
            }
        } else if (Report.Location.Right.equals(cell.getLocation())) {
            if (index > 0) {
                CellPanel previousCell = (CellPanel) getWidget(index - 1);
                if (previousCell.isSpaceHolder()) {
                    remove(cell);
                    remove(previousCell);
                } else {
                    cell.setSpaceHolder();
                }
            }
        }
    }

    public void removeGadget(Widget widget) {
        for (int i = 0; i < getWidgetCount(); i++) {
            if (widget != null && widget.equals(((CellPanel) getWidget(i)).getWidget())) {
                removeGadget(i);
            }
        }
    }

    public Widget getGadget(int index) {
        if (checkIndex(index)) {
            return null;
        }

        CellPanel cell = (CellPanel) getWidget(index);
        return (cell == null ? null : cell.getWidget());
    }

    public Report.Location getGadgetLocation(Widget widget) {
        for (int i = 0; i < getWidgetCount(); i++) {
            CellPanel cellPanel = (CellPanel) getWidget(i);
            if (widget != null && widget.equals(cellPanel.getWidget())) {
                return cellPanel.getLocation();
            }
        }
        return null;
    }

    public int getGadgetIndex(Widget widget) {
        for (int i = 0; i < getWidgetCount(); i++) {
            if (widget != null && widget.equals(((CellPanel) getWidget(i)).getWidget())) {
                return i;
            }
        }
        return -1;
    }

    public int getGadgetIndex(int mouseX, int mouseY) {
        for (int i = 0; i < getWidgetCount(); i++) {
            CellPanel cellPanel = (CellPanel) getWidget(i);
            WidgetArea cellArea = new WidgetArea(cellPanel, null);
            if (cellArea.getLeft() <= mouseX && mouseX <= cellArea.getRight() && cellArea.getTop() <= mouseY && mouseY <= cellArea.getBottom()) {
                return i;
            }
        }
        return -1;
    }

    public void replaceGadget(Widget widget, Widget widgetReplaceTo) {
        for (int i = 0; i < getWidgetCount(); i++) {
            if (widget != null && widget.equals(((CellPanel) getWidget(i)).getWidget())) {
                ((CellPanel) getWidget(i)).setWidget(widgetReplaceTo);
            }
        }
    }

    private boolean checkIndex(int index) {
        if (index < 0 || index > getWidgetCount()) {
            log.debug("Wrong index - {}", index);
            return false;
        }
        return true;
    }

    protected class CellPanel extends SimplePanel {

        private Report.Location location;

        public CellPanel(Report.Location location) {
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            setLocation(location);
            setWidget(new SpaceHolder());
        }

        public boolean isSpaceHolder() {
            return getWidget() == null || getWidget() instanceof SpaceHolder;
        }

        public boolean isPositioner() {
            return getWidget() == null || getWidget() instanceof ReportGadgetPositioner;
        }

        public void setSpaceHolder() {
            setWidget(new SpaceHolder());
        }

        public void setLocation(Report.Location location) {
            this.location = location;
            switch (location) {
            case Left:
            case Right:
                setWidth("50%");
                break;
            case Full:
                setWidth("100%");
                break;
            default:
                break;
            }
        }

        public Report.Location getLocation() {
            return location;
        }

        class SpaceHolder extends HTML {

            SpaceHolder() {
                super("&nbsp;");
            }
        }
    }

    @Override
    public void onEvent(Reason reason) {
        handler.onEvent(reason);
    }

    // Iteration stuff:
    private class GadgetIterator implements IGadgetIterator {

        private int index = -1;

        @Override
        public boolean hasNext() {
            return checkIndex(index + 1);
        }

        @Override
        public IGadget next() {
            Widget w = getGadget(++index);
            if (w == null || !(w instanceof GadgetHolder)) {
                throw new NoSuchElementException();
            }
            return ((GadgetHolder) w).getGadget();
        }

        @Override
        public void remove() {
            if (!checkIndex(index)) {
                throw new NoSuchElementException();
            }
            removeGadget(index--);
        }

        @Override
        public int getColumn() {
            if (!checkIndex(index)) {
                throw new NoSuchElementException();
            }

            int col = -1;
            switch (getGadgetLocation(getGadget(index))) {
            case Full:
                col = -1;
                break;
            case Left:
                col = 0;
                break;
            case Right:
                col = 1;
                break;
            }
            return col;
        }
    }

    public IGadgetIterator getGadgetIterator() {
        return new GadgetIterator();
    }
}
