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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportLayoutPanel extends FlowPanel {

    public ReportLayoutPanel() {
        getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        setWidth("100%");
    }

    public void addGadget(Widget widget, boolean fullWidth) {
        insertGadget(widget, fullWidth, getWidgetCount());
    }

    public void insertGadget(Widget widget, boolean fullWidth, int beforeIndex) {
        CellPanel cell = new CellPanel(fullWidth);
        cell.setWidget(widget);
        insert(cell, beforeIndex);
    }

    public void setGadget(Widget widget, boolean fullWidth, int index) {
        CellPanel cell = new CellPanel(fullWidth);
        cell.setWidget(widget);
        remove(index);
        insert(cell, index);
    }

    public void removeGadget(int index) {
        remove(index);
    }

    public void removeGadget(Widget widget) {
        for (int i = 0; i < getWidgetCount(); i++) {
            if (widget != null && widget.equals(((CellPanel) getWidget(i)).getWidget())) {
                remove(i);
            }
        }
    }

    public Widget getGadget(int index) {
        CellPanel cell = (CellPanel) getWidget(index);
        return cell == null ? null : cell.getWidget();
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

    public int getInsertionIndex(int mouseX, int mouseY) {
        for (int i = 0; i < getWidgetCount(); i++) {
            CellPanel cellPanel = (CellPanel) getWidget(i);
            WidgetArea cellArea = new WidgetArea(cellPanel, null);
            if (cellArea.getLeft() <= mouseX && mouseX <= cellArea.getRight() && cellArea.getTop() <= mouseY && mouseY <= cellArea.getBottom()) {
                return i;
            }
        }
        return -1;
    }

    class CellPanel extends SimplePanel {

        public CellPanel(boolean fullWidth) {
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            if (fullWidth) {
                setWidth("100%");
            } else {
                setWidth("50%");
            }
        }

    }

}
