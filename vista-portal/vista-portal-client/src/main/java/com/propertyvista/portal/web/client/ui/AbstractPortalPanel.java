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
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.LabelPosition;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.web.client.ui.util.decorators.PortalWidgetDecorator;

public abstract class AbstractPortalPanel extends SimplePanel {

    private LabelPosition widgetLayout = null;

    public AbstractPortalPanel() {
        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout();
            }

        });
    }

    public void doLayout() {
        LabelPosition newWdgetLayout = getWidgetLayout();
        if (widgetLayout != newWdgetLayout) {
            updateDecoratorsLayout(AbstractPortalPanel.this, newWdgetLayout);
            widgetLayout = newWdgetLayout;
        }
    }

    public static LabelPosition getWidgetLayout() {
        LabelPosition layout;
        switch (LayoutType.getLayoutType(Window.getClientWidth())) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            layout = LabelPosition.top;
            break;

        case tabletLandscape:
        case monitor:
        case huge:
            layout = LabelPosition.left;
            break;
        default:
            layout = LabelPosition.left;
        }
        return layout;
    }

    public static void updateDecoratorsLayout(Widget widget, LabelPosition layout) {
        if (widget instanceof PortalWidgetDecorator) {
            PortalWidgetDecorator decorator = (PortalWidgetDecorator) widget;
            if (decorator.getLabelPosition() != LabelPosition.hidden) {
                decorator.setLabelPosition(layout);
            }
        }
        if (widget instanceof HasWidgets) {
            for (Widget childWidget : (HasWidgets) widget) {
                updateDecoratorsLayout(childWidget, layout);
            }
        }
    }

}
