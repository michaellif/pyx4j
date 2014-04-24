/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 11, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.decorators;

import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CTextFieldBase;

public class LoginWidgetDecorator extends FormWidgetDecorator {

    private final LoginWidgetDecoratorBuilder builder;

    protected LoginWidgetDecorator(LoginWidgetDecoratorBuilder builder) {
        super(builder);
        this.builder = builder;
    }

    @Override
    public void init(CField<?, ?> component) {
        String text = builder.getWatermark() != null ? builder.getWatermark() : component.getTitle();
        if (component instanceof CTextFieldBase) {
            ((CTextFieldBase<?, ?>) component).setWatermark(text);
        } else if (component instanceof CCaptcha) {
            ((CCaptcha) component).setWatermark(text);
        }
        super.init(component);
    }

}
