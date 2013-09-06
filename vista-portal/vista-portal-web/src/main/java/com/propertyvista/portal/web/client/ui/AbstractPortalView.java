/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 6, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

public abstract class AbstractPortalView extends SimplePanel implements IsView {

    private Layout widgetLayout = Layout.horisontal;

    public AbstractPortalView() {
        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout();
            }

        });
    }

    public void doLayout() {
        Layout newWdgetLayout = getWidgetLayout();
        if (widgetLayout != newWdgetLayout) {
            updateDecoratorsLayout(getCContainer(), newWdgetLayout);
            widgetLayout = newWdgetLayout;
        }
    }

    public Layout getWidgetLayout() {
        Layout layout;
        switch (LayoutType.getLayoutType(Window.getClientWidth())) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            layout = Layout.vertical;
            break;

        case tabletLandscape:
        case monitor:
        case huge:
            layout = Layout.horisontal;
            break;
        default:
            layout = Layout.horisontal;
        }
        return layout;
    }

    public abstract CEntityContainer<?> getCContainer();

    public static void updateDecoratorsLayout(CContainer<?> container, Layout layout) {
        if (container.getComponents() == null) {
            return;
        }
        for (CComponent<?> component : container.getComponents()) {
            if (component.getDecorator() instanceof WidgetDecorator) {
                WidgetDecorator decorator = (WidgetDecorator) component.getDecorator();
                decorator.setLayout(layout);
            }
            if (component instanceof CContainer) {
                updateDecoratorsLayout((CContainer<?>) component, layout);
            }
        }
    }
}
