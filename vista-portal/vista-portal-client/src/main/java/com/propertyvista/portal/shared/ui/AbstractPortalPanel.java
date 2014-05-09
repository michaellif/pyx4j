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
package com.propertyvista.portal.shared.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel;
import com.pyx4j.gwt.commons.css.CssVariable;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecorator;

public abstract class AbstractPortalPanel extends SimplePanel {

    private LabelPosition labelPosition = null;

    public AbstractPortalPanel() {
        CssVariable.setVariable(getElement(), DualColumnFluidPanel.CSS_VAR_FORM_COLLAPSING_LAYOUT_TYPE, LayoutType.huge.name());

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout();
            }

        });
    }

    public void doLayout() {
        LabelPosition newLabelPosition = getWidgetLabelPosition();
        if (labelPosition != newLabelPosition) {
            updateDecoratorsLabelPosition(this, newLabelPosition);
            labelPosition = newLabelPosition;
        }
    }

    public static LabelPosition getWidgetLabelPosition() {
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

    private void updateDecoratorsLabelPosition(Widget widget, LabelPosition layout) {
        if (widget instanceof FormWidgetDecorator) {
            FormWidgetDecorator decorator = (FormWidgetDecorator) widget;
            if (decorator.getLabelPosition() != LabelPosition.hidden) {
                decorator.setLabelPosition(layout);
            }
        }
        if (widget instanceof HasWidgets) {
            for (Widget childWidget : (HasWidgets) widget) {
                updateDecoratorsLabelPosition(childWidget, layout);
            }
        }
    }

}
