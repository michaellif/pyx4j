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

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

public class LoginPanelWidgetDecorator extends WidgetDecorator {

    public LoginPanelWidgetDecorator(CComponent<?> component) {
        super(new Builder(component).labelWidth(9).componentWidth(16).labelAlignment(Builder.Alignment.left));
        addStyleDependentName(WidgetDecorator.StyleDependent.noMandatoryStar.name());

    }

}
