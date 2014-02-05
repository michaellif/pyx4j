/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.decorators;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

public class RadioButtonGroupDecoratorBuilder extends WidgetDecorator.Builder {

    public RadioButtonGroupDecoratorBuilder(CComponent<?> component) {
        super(component);
        contentWidth("100%");
        componentWidth(70 + "px");
        labelPosition(LabelPosition.top);
        useLabelSemicolon(false);
        labelAlignment(Alignment.left);
    }
}