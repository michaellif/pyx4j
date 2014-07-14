/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;

public class LoginPanelWidgetDecorator extends FieldDecorator {

    public LoginPanelWidgetDecorator() {
        this(16);
    }

    public LoginPanelWidgetDecorator(double componentWidth) {
        super(new Builder().labelWidth(9 + "em").componentWidth(componentWidth + "em").labelAlignment(Builder.Alignment.left));
        addStyleDependentName(WidgetDecoratorTheme.StyleDependent.noMandatoryStar.name());
    }
}
