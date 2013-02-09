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
 * Created on Oct 6, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.field.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AppActivityManager;
import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.field.client.theme.FieldPalette;
import com.propertyvista.field.client.theme.FieldTheme;

public class FieldPanel extends LayoutPanel {

    public FieldPanel() {

        EventBus eventBus = AppSite.getEventBus();

        StyleManger.installTheme(new FieldTheme(), new FieldPalette());
        setStyleName(FieldTheme.StyleName.SiteView.name());

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setStyleName(FieldTheme.StyleName.SiteViewContent.name());
        add(contentPanel);

        //============ Header Panel ============
        FlowPanel headerPanel = new FlowPanel();
        headerPanel.setStyleName(FieldTheme.StyleName.SiteViewHeader.name());
        contentPanel.addNorth(headerPanel, 5);

        //============ Main application area ============
        LayoutPanel centerPanel = new LayoutPanel();
        contentPanel.add(centerPanel);

        //============ Main application area content: lister (left) and details view (right) =======
        SplitLayoutPanel splitPanel = new SplitLayoutPanel(2);
        splitPanel.setSize("100%", "100%");
        centerPanel.add(splitPanel);

        DisplayPanel listerDisplay = new DisplayPanel();
        listerDisplay.setSize("100%", "100%");
        splitPanel.add(listerDisplay);

        DisplayPanel detailsDisplay = new DisplayPanel();
        detailsDisplay.setSize("100%", "100%");
        splitPanel.add(detailsDisplay);

        //============ Footer Panel ============
        FlowPanel footerPanel = new FlowPanel();
        footerPanel.setStyleName(FieldTheme.StyleName.SiteViewFooter.name());
        contentPanel.addSouth(footerPanel, 5);

        // Activity <-> Display bindings:
//        bind(new NavigActivityMapper(), listerDisplay, eventBus);
//        bind(new ConsoleActivityMapper(), consoleDisplay, eventBus);
//        bind(new MainActivityMapper(), mainDisplay, eventBus);
    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    private static void bind(AppActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        AppActivityManager activityManager = new AppActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    class DisplayPanel extends SimplePanel implements RequiresResize, ProvidesResize {
        DisplayPanel() {
            setStyleName(FieldTheme.StyleName.SiteViewDisplay.name());
        }

        @Override
        public void onResize() {
            Widget child = getWidget();
            if ((child != null) && (child instanceof RequiresResize)) {
                ((RequiresResize) child).onResize();
            }
        }

        @Override
        public void setWidget(IsWidget w) {
            super.setWidget(w);
            //centerAreaContent.setVisible(true);
        }
    }

    class UtilityDisplayPanel extends DisplayPanel {

        private final LayoutPanel parent;

        UtilityDisplayPanel(LayoutPanel parent) {
            this.parent = parent;
            setStyleName(CrmSitePanelTheme.StyleName.SiteViewDisplay.name());
        }

        @Override
        public void setWidget(IsWidget w) {
            super.setWidget(w);
            if (w == null) {
                removeFromParent();
                for (int i = 0; i < parent.getWidgetCount(); i++) {
                    parent.getWidget(i).setVisible(true);
                }
            } else {
                for (int i = 0; i < parent.getWidgetCount(); i++) {
                    parent.getWidget(i).setVisible(false);
                }
                parent.add(this);
            }
        }

    }
}
