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

import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportLayoutPanel extends FlowPanel {

    public ReportLayoutPanel() {
        getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        setWidth("100%");
    }

    public void addGadget(Widget widget, Report.Location location) {
        insertGadget(widget, location, getWidgetCount());
    }

    public void insertGadget(Widget widget, Report.Location location, int beforeIndex) {
        if (beforeIndex < 0 || beforeIndex > getWidgetCount()) {
            System.out.println("Wrong beforeIndex - " + beforeIndex);
            return;
        }

        CellPanel beforeCell = null;
        Report.Location beforeCellLocation = null;
        boolean isBeforeCellSpaceHolder = false;
        if (beforeIndex < getWidgetCount()) {
            beforeCell = (CellPanel) getWidget(beforeIndex);
            beforeCellLocation = beforeCell.getLocation();
            isBeforeCellSpaceHolder = beforeCell.isSpaceHolder();
        }

        CellPanel afterCell = null;
        Report.Location afterCellLocation = null;
        boolean isAfterCellSpaceHolder = false;
        if (beforeIndex > 0) {
            afterCell = (CellPanel) getWidget(beforeIndex - 1);
            afterCellLocation = afterCell.getLocation();
            isAfterCellSpaceHolder = afterCell.isSpaceHolder();
        }

        // try to find empty cell in current rows:
        boolean anyPlace = (!Report.Location.Full.equals(location) && Report.Location.Any.equals(location));

        if (isBeforeCellSpaceHolder && (location.equals(beforeCellLocation) || anyPlace)) {
            beforeCell.setWidget(widget);
            return;
        }

        if (isAfterCellSpaceHolder && (location.equals(afterCellLocation) || anyPlace)) {
            afterCell.setWidget(widget);
            return;
        }

        // ok, create new row:
        CellPanel cell = new CellPanel(location);
        cell.setWidget(widget);

        if (Report.Location.Right.equals(beforeCellLocation)) {
            beforeIndex = beforeIndex - 1;
        }

        switch (location) {
        case Any:
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
            break;
        }
    }

    public void setGadget(Widget widget, int index) {
        CellPanel cell = (CellPanel) getWidget(index);
        cell.setWidget(widget);
    }

    public void removeGadget(int index) {
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
        CellPanel cell = (CellPanel) getWidget(index);
        return cell == null ? null : cell.getWidget();
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

    class CellPanel extends SimplePanel {

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

}
