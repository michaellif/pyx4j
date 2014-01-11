/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.decorators;

import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.portal.shared.ui.AbstractPortalPanel;

public class FormWidgetDecoratorBuilder extends FormWidgetDecorator.Builder {

    // default sizes (in pixels): 
    public static final int LABEL_WIDTH = 220;

    public static final int CONTENT_WIDTH = 250;

    public FormWidgetDecoratorBuilder(CComponent<?> component, String labelWidth, String componentWidth, String contentWidth) {
        super(component);
        labelWidth(labelWidth);
        contentWidth(contentWidth);
        componentWidth(componentWidth);
        labelPosition(AbstractPortalPanel.getWidgetLabelPosition());
    }

    public FormWidgetDecoratorBuilder(CComponent<?> component, int labelWidth, int componentWidth, int contentWidth) {
        this(component, labelWidth + "px", componentWidth + "px", contentWidth + "px");
    }

    public FormWidgetDecoratorBuilder(CComponent<?> component, int labelWidth, int componentWidth) {
        this(component, labelWidth, componentWidth, CONTENT_WIDTH);
    }

    public FormWidgetDecoratorBuilder(CComponent<?> component, int componentWidth) {
        this(component, LABEL_WIDTH, componentWidth);
    }

    public FormWidgetDecoratorBuilder(CComponent<?> component) {
        this(component, CONTENT_WIDTH);
    }

    @Override
    public FormWidgetDecorator build() {
        return new FormWidgetDecorator(this) {
            @Override
            protected void updateViewable() {
                if (getLabelPosition() != LabelPosition.top) {
                    if (getComnponent().isViewable()) {
                        labelAlignment(Alignment.left);
                        useLabelSemicolon(false);
                    } else {
                        labelAlignment(Alignment.right);
                        useLabelSemicolon(true);
                    }
                }
                updateCaption();
                updateLabelAlignment();
                super.updateViewable();
            }
        };
    }

}
