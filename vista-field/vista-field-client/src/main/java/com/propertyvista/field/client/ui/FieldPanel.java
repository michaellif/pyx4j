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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.field.client.mvp.DetailsActivityMapper;
import com.propertyvista.field.client.mvp.ListerActivityMapper;
import com.propertyvista.field.client.mvp.ScreenActivityMapper;
import com.propertyvista.field.client.mvp.ToolbarActivityMapper;
import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;

public class FieldPanel extends LayoutPanel {

    public FieldPanel() {

        setStyleName(FieldTheme.StyleName.SiteView.name());

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setStyleName(FieldTheme.StyleName.SiteViewContent.name());
        add(contentPanel);

        //============ Screen display ============
        DisplayPanel screenDisplay = new ScreenDisplayPanel();
        screenDisplay.setSize("100%", "100%");
        contentPanel.add(screenDisplay);

        //============ Composite view with all displays 
        FieldScreenView screen = FieldViewFactory.instance(FieldScreenView.class);
        screenDisplay.add(screen);

        EventBus eventBus = AppSite.getEventBus();

        //Activity <-> Display bindings:
        bind(new ScreenActivityMapper(), screenDisplay, eventBus);
        bind(new ToolbarActivityMapper(), screen.getToolbarDisplay(), eventBus);
        bind(new ListerActivityMapper(), screen.getListerDisplay(), eventBus);
        bind(new DetailsActivityMapper(), screen.getDetailsDisplay(), eventBus);
    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager activityManager = new ActivityManager(mapper, eventBus);
        activityManager.setDisplay(widget);
    }

    class ScreenDisplayPanel extends DisplayPanel {

        @Override
        public void setWidget(IsWidget w) {
            if (getWidget() != null && getWidget() instanceof FieldScreenView) {
                ((FieldScreenView) getWidget()).setWidget(w);
            }
        }
    }

}
