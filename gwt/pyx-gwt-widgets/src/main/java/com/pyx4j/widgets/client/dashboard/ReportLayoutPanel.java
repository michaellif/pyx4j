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

    public void addGadget(Widget widget, int column) {
        insertGadget(widget, getWidgetCount(), column);
    }

    public void insertGadget(Widget widget, int row, int column) {
        if (row > getWidgetCount()) {
            throw new Error("Row is out of bounds");
        } else if (column == -1) { //column -1 means full width
            insert(widget, row);
        } else {
            if (row == getWidgetCount()) {
                RowPanel rowPanel = new RowPanel();
                if (column == 0) {
                    rowPanel.setLeftGadget(widget);
                } else if (column == 1) {
                    rowPanel.setRightGadget(widget);
                } else {
                    throw new Error("Column number can be -1, 0 or 1");
                }
                insert(rowPanel, row);
            } else {
                Widget currentRow = getWidget(row);
                RowPanel rowPanel = null;
                if (column == 0) {
                    if (currentRow instanceof RowPanel && !((RowPanel) currentRow).hasLeft()) {
                        rowPanel = (RowPanel) currentRow;
                    } else {
                        rowPanel = new RowPanel();
                        insert(rowPanel, row);
                    }
                    rowPanel.setLeftGadget(widget);
                } else if (column == 1) {
                    if (currentRow instanceof RowPanel && !((RowPanel) currentRow).hasRight()) {
                        rowPanel = (RowPanel) currentRow;
                    } else {
                        rowPanel = new RowPanel();
                        insert(rowPanel, row);
                    }
                    rowPanel.setRightGadget(widget);
                } else {
                    throw new Error("Column number can be -1, 0 or 1");
                }

            }
        }

    }

    public void removeGadget(int row, int column) {

    }

    public Widget getGadget(int row, int column) {
        return null;
    }

    class RowPanel extends FlowPanel {

        private final SimplePanel left;

        private final SimplePanel right;

        RowPanel() {
            setWidth("100%");
            left = new SimplePanel();
            left.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            left.setWidth("50%");
            add(left);
            right = new SimplePanel();
            right.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            right.setWidth("50%");
            add(right);
        }

        void setLeftGadget(Widget widget) {
            left.setWidget(widget);
        }

        Widget getLeftGadget() {
            return left.getWidget();
        }

        void setRightGadget(Widget widget) {
            right.setWidget(widget);
        }

        Widget getRightGadget() {
            return right.getWidget();
        }

        boolean hasLeft() {
            return left.getWidget() != null;
        }

        boolean hasRight() {
            return right.getWidget() != null;
        }

    }

}
