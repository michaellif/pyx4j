/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Apr 25, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class TwoColumnFluidPanel implements IsWidget {

    public static enum Location {
        Dual, Left, Right
    }

    private final FlowPanel contentPanel;

    private boolean collapsed;

    public TwoColumnFluidPanel() {
        contentPanel = new FlowPanel();
    }

    private CellPanel ensureCellPanel(Location location) {
        CellPanel panel = null;

        int widgetCount = contentPanel.getWidgetCount();
        if (widgetCount == 0) {
            switch (location) {
            case Dual:
                panel = new CellPanel(Location.Dual);
                contentPanel.add(panel);
                break;
            case Left:
                panel = new CellPanel(Location.Left);
                contentPanel.add(panel);
                break;
            case Right:
                panel = new CellPanel(Location.Right);
                contentPanel.add(new CellPanel(Location.Left));
                contentPanel.add(panel);
                break;
            }
        } else {
            panel = (CellPanel) contentPanel.getWidget(widgetCount - 1);
            if (panel.location != location) {
                switch (location) {
                case Dual:
                    panel = new CellPanel(Location.Dual);
                    contentPanel.add(panel);
                    break;
                case Left:
                    if (panel.location == Location.Right) {
                        panel = (CellPanel) contentPanel.getWidget(widgetCount - 2);
                    } else if (panel.location == Location.Dual) {
                        panel = new CellPanel(Location.Left);
                        contentPanel.add(panel);
                    }
                    break;
                case Right:
                    if (panel.location == Location.Left) {
                        panel = new CellPanel(Location.Right);
                        contentPanel.add(panel);
                    } else if (panel.location == Location.Dual) {
                        panel = new CellPanel(Location.Right);
                        contentPanel.add(new CellPanel(Location.Left));
                        contentPanel.add(panel);
                    }
                    break;
                }
            }
        }
        return panel;
    }

    public void append(Location location, IsWidget widget) {
        ensureCellPanel(location).add(widget);
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    public void setVisible(boolean visible) {
        contentPanel.setVisible(visible);
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
            Widget widget = contentPanel.getWidget(i);
            if (widget instanceof CellPanel) {
                ((CellPanel) widget).adjustWidth(collapsed);
            }
        }
    }

    protected class CellPanel extends FlowPanel {

        private final Location location;

        public CellPanel(Location location) {
            this.location = location;
            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            adjustWidth(collapsed);
        }

        private void adjustWidth(boolean collapsed) {
            switch (location) {
            case Left:
            case Right:
                if (collapsed) {
                    setWidth("100%");
                } else {
                    setWidth("50%");
                }
                break;
            case Dual:
                setWidth("100%");
                break;
            default:
                break;
            }
        }

        public Location getLocation() {
            return location;
        }

    }

}
