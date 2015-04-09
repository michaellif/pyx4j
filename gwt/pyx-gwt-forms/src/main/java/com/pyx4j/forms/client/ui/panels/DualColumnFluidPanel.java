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
 */
package com.pyx4j.forms.client.ui.panels;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.gwt.commons.css.CssVariable;
import com.pyx4j.gwt.commons.layout.ILayoutable;
import com.pyx4j.gwt.commons.layout.LayoutType;

public class DualColumnFluidPanel implements IsWidget {

    public static final String CSS_VAR_FORM_COLLAPSING_LAYOUT_TYPE = "FormCollapsingLayoutType";

    public static enum Location {
        Dual, Left, Right
    }

    private final ContentPanel contentPanel;

    public DualColumnFluidPanel() {
        contentPanel = new ContentPanel();
    }

    private BlockPanel ensureCellPanel(Location location) {
        BlockPanel panel = null;

        int widgetCount = contentPanel.getWidgetCount();
        if (widgetCount == 0) {
            switch (location) {
            case Dual:
                panel = new BlockPanel(Location.Dual);
                contentPanel.add(panel);
                break;
            case Left:
                panel = new BlockPanel(Location.Left);
                contentPanel.add(panel);
                contentPanel.add(new BlockPanel(Location.Right));
                break;
            case Right:
                panel = new BlockPanel(Location.Right);
                contentPanel.add(new BlockPanel(Location.Left));
                contentPanel.add(panel);
                break;
            }
        } else {
            panel = (BlockPanel) contentPanel.getWidget(widgetCount - 1);
            if (panel.location != location) {
                switch (location) {
                case Dual:
                    panel = new BlockPanel(Location.Dual);
                    contentPanel.add(panel);
                    break;
                case Left:
                    if (panel.location == Location.Right) {
                        panel = (BlockPanel) contentPanel.getWidget(widgetCount - 2);
                    } else if (panel.location == Location.Dual) {
                        panel = new BlockPanel(Location.Left);
                        contentPanel.add(panel);
                        contentPanel.add(new BlockPanel(Location.Right));
                    }
                    break;
                case Right:
                    if (panel.location == Location.Left) {
                        panel = new BlockPanel(Location.Right);
                        contentPanel.add(panel);
                    } else if (panel.location == Location.Dual) {
                        panel = new BlockPanel(Location.Right);
                        contentPanel.add(new BlockPanel(Location.Left));
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

    public void addStyleName(String style) {
        contentPanel.addStyleName(style);
    }

    public void clear() {
        contentPanel.clear();
    }

    protected class ContentPanel extends FlowPanel implements ILayoutable {

        public ContentPanel() {
            setStyleName(FormPanelTheme.StyleName.FluidPanel.name());
        }

        @Override
        public void doLayout(LayoutType type) {
            String var = CssVariable.getVariable(getElement(), CSS_VAR_FORM_COLLAPSING_LAYOUT_TYPE);
            if (var == null) {
                setCollapsed(true);
            } else {
                LayoutType collapseType = LayoutType.valueOf(var);
                if (collapseType != null) {
                    setCollapsed(collapseType.compareTo(type) >= 0);
                }
            }
        }

        public void setCollapsed(boolean collapsed) {
            if (collapsed) {
                contentPanel.addStyleDependentName(FormPanelTheme.StyleDependent.collapsed.name());
            } else {
                contentPanel.removeStyleDependentName(FormPanelTheme.StyleDependent.collapsed.name());
            }
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
        }
    }

    protected class BlockPanel extends FlowPanel {

        private final Location location;

        public BlockPanel(Location location) {
            this.location = location;
            setStyleName(FormPanelTheme.StyleName.FluidPanelBlock.name());
            switch (location) {
            case Left:
                addStyleDependentName(FormPanelTheme.StyleDependent.left.name());
                break;
            case Right:
                addStyleDependentName(FormPanelTheme.StyleDependent.right.name());
                break;
            case Dual:
                addStyleDependentName(FormPanelTheme.StyleDependent.dual.name());
                break;
            }
        }

        public Location getLocation() {
            return location;
        }

    }

}
