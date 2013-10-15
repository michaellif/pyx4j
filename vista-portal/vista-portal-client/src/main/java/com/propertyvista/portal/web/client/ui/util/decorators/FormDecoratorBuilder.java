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
package com.propertyvista.portal.web.client.ui.util.decorators;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

import com.propertyvista.portal.web.client.ui.AbstractPortalPanel;

public class FormDecoratorBuilder extends WidgetDecorator.Builder {

    // While changing any value(s) ensure all others consistency!   
    public static final String LABEL_WIDTH = "220px";

    public static final String LABEL_WIDTH_HALF = "110px";

    public static final String CONTENT_WIDTH = "250px";

    public static final String FULL_WIDTH = "470px";

    public FormDecoratorBuilder(CComponent<?> component, String labelWidth, String componentWidth, String contentWidth) {
        super(component);
        labelWidth(labelWidth);
        contentWidth(contentWidth);
        componentWidth(componentWidth);
        layout(AbstractPortalPanel.getWidgetLayout());
    }

    @Override
    public WidgetDecorator build() {
        WidgetDecorator decorator = new WidgetDecorator(this) {
            @Override
            protected void updateViewable() {
                if (getComnponent().isViewable()) {
                    labelAlignment(Alignment.left);
                    useLabelSemicolon(false);
                } else {
                    labelAlignment(Alignment.right);
                    useLabelSemicolon(true);
                }
                updateCaption();
                updateLabelAlignment();
                super.updateViewable();
            }
        };
        return decorator;
    }

    public FormDecoratorBuilder(CComponent<?> component, int componentWidth) {
        this(component, componentWidth + "px");
    }

    public FormDecoratorBuilder(CComponent<?> component, String componentWidth) {
        this(component, LABEL_WIDTH, componentWidth, CONTENT_WIDTH);
    }

    public FormDecoratorBuilder(CComponent<?> component, int labelWidth, int componentWidth) {
        this(component, labelWidth + "px", componentWidth + "px", CONTENT_WIDTH);
    }

    public FormDecoratorBuilder(CComponent<?> component, int labelWidth, int componentWidth, int contentWidth) {
        this(component, labelWidth + "px", componentWidth + "px", contentWidth + "px");
    }

    public FormDecoratorBuilder(CComponent<?> component) {
        this(component, LABEL_WIDTH, CONTENT_WIDTH, CONTENT_WIDTH);
    }

}
