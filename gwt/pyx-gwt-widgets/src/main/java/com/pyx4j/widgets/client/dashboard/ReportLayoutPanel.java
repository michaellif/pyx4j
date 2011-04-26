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

    public void insertGadget(Widget widget, int beforeRow, int column) {
        if (beforeRow > getWidgetCount()) {
            throw new Error("Row is out of bounds");
        } else if (column == -1) { //column -1 means full width
            insert(widget, beforeRow);
        } else {
            Widget currentRow = getWidget(beforeRow - 1);
            RowPanel rowPanel = null;
            if (column == 0) {
                if (currentRow instanceof RowPanel && !((RowPanel) currentRow).hasLeft()) {
                    rowPanel = (RowPanel) currentRow;
                } else {
                    rowPanel = new RowPanel();
                    insert(rowPanel, beforeRow);
                }
                rowPanel.setLeftGadget(widget);
            } else if (column == 1) {
                if (currentRow instanceof RowPanel && !((RowPanel) currentRow).hasRight()) {
                    rowPanel = (RowPanel) currentRow;
                } else {
                    rowPanel = new RowPanel();
                    insert(rowPanel, beforeRow);
                }
                rowPanel.setRightGadget(widget);
            } else {
                throw new Error("Column number can be -1, 0 or 1");
            }

        }

    }

    public void setGadget(Widget widget, int row, int column) {
        if (row > getWidgetCount()) {
            throw new Error("Row is out of bounds");
        } else if (column == -1) { //column -1 means full width
            remove(row);
            insert(widget, row);
        } else {
            Widget currentRow = getWidget(row);
            RowPanel rowPanel = null;
            if (column == 0) {
                if (currentRow instanceof RowPanel) {
                    rowPanel = (RowPanel) currentRow;
                    rowPanel.setLeftGadget(widget);
                }
            } else if (column == 1) {
                if (currentRow instanceof RowPanel) {
                    rowPanel = (RowPanel) currentRow;
                    rowPanel.setRightGadget(widget);
                }
            } else {
                throw new Error("Column number can be -1, 0 or 1");
            }

        }

    }

    public void removeGadget(Widget widget) {
        int row = getGadgetRowIndex(widget);
        int column = getGadgetColumnIndex(widget);
        removeGadget(row, column);
    }

    public void removeGadget(int row, int column) {
        Widget rowWidget = getWidget(row);
        if (column == -1) {
            if (rowWidget instanceof RowPanel) {
                throw new Error("Column -1 should represent whole width gadget");
            }
            remove(rowWidget);
        } else if (rowWidget instanceof RowPanel) {
            RowPanel rowPanel = (RowPanel) rowWidget;
            if (column == 0) {
                rowPanel.removeLeftGadget();
            } else if (column == 1) {
                rowPanel.removeRightGadget();
            } else {
                throw new Error("Column number can be -1, 0 or 1");
            }
        } else {
            throw new Error("Gadget coordinates are wrong: " + row + "/" + column);
        }
    }

    public Widget getGadget(int row, int column) {
        Widget rowWidget = getWidget(row);
        if (column == -1) {
            if (rowWidget instanceof RowPanel) {
                throw new Error("Column -1 should represent whole width gadget");
            }
            return rowWidget;
        } else if (rowWidget instanceof RowPanel) {
            RowPanel rowPanel = (RowPanel) rowWidget;
            if (column == 0) {
                return rowPanel.getLeftGadget();
            } else if (column == 1) {
                return rowPanel.getRightGadget();
            } else {
                throw new Error("Column number can be -1, 0 or 1");
            }
        } else {
            throw new Error("Gadget coordinates are wrong: " + row + "/" + column);
        }
    }

    public int getGadgetRowIndex(Widget gadget) {
        if (gadget == null) {
            return -1;
        }
        int index = getWidgetIndex(gadget);
        if (index > -1) {
            return index;
        }
        for (int i = 0; i < getWidgetCount(); i++) {
            if (getWidget(i) instanceof RowPanel) {
                RowPanel rowPanel = (RowPanel) getWidget(i);
                if (gadget.equals(rowPanel.getLeftGadget()) || gadget.equals(rowPanel.getRightGadget())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getGadgetColumnIndex(Widget gadget) {
        if (gadget == null) {
            return -1;
        }
        int index = getWidgetIndex(gadget);
        if (index > -1) {
            return -1;
        }
        for (int i = 0; i < getWidgetCount(); i++) {
            if (getWidget(i) instanceof RowPanel) {
                RowPanel rowPanel = (RowPanel) getWidget(i);
                if (gadget.equals(rowPanel.getLeftGadget())) {
                    return 0;
                } else if (gadget.equals(rowPanel.getRightGadget())) {
                    return 1;
                }
            }
        }
        return -1;
    }

    class RowPanel extends FlowPanel {

        private final CellPanel left;

        private final CellPanel right;

        RowPanel() {
            setWidth("100%");
            left = new CellPanel();
            left.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            left.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);

            left.setWidth("50%");
            add(left);
            right = new CellPanel();
            right.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            right.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
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

        void removeLeftGadget() {
            left.clear();

        }

        void removeRightGadget() {
            right.clear();
        }

        void removeFromParentIfEmpty() {
            if (!hasLeft() && !hasRight()) {
                removeFromParent();
            }
        }

        class CellPanel extends SimplePanel {
            @Override
            public boolean remove(Widget child) {
                boolean removed = super.remove(child);
                removeFromParentIfEmpty();
                return removed;
            }

        }

    }

}
