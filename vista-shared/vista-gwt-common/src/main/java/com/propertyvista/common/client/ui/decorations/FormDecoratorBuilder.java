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
package com.propertyvista.common.client.ui.decorations;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

public class FormDecoratorBuilder extends WidgetDecorator.Builder {

    public FormDecoratorBuilder(CComponent<?> component, String labelWidth, String componentWidth, String contentWidth) {
        super(component);
        labelWidth(labelWidth);
        contentWidth(contentWidth);
        componentWidth(componentWidth);
        if (component.isViewable()) {
            labelAlignment(Alignment.left);
            useLabelSemicolon(false);
        } else {
            labelAlignment(Alignment.right);
            useLabelSemicolon(true);
        }

    }

    public FormDecoratorBuilder(CComponent<?> component, int labelWidth, int componentWidth, int contentWidth) {
        this(component, labelWidth + "em", componentWidth + "em", contentWidth + "em");
    }

    public FormDecoratorBuilder(CComponent<?> component, int componentWidth, boolean dual) {
        this(component, "15em", componentWidth + "em", dual ? "65em" : "25em");
        // assert dual && componentWidth <= 58 || !dual && componentWidth <= 23;
    }

    public FormDecoratorBuilder(CComponent<?> component, int componentWidth) {
        this(component, componentWidth, false);
    }

    public FormDecoratorBuilder(CComponent<?> component, boolean dual) {
        this(component, 16, dual);
    }

    public FormDecoratorBuilder(CComponent<?> component) {
        this(component, false);
    }
}
