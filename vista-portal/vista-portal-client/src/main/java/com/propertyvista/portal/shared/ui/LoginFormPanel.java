/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.IAcceptsWatermark;
import com.pyx4j.forms.client.ui.panels.FormFieldDecorator;
import com.pyx4j.forms.client.ui.panels.FormFieldDecoratorOptions;
import com.pyx4j.forms.client.ui.panels.FormPanel;

public class LoginFormPanel extends FormPanel {

    public LoginFormPanel(CForm<?> parent) {
        super(parent);
    }

    @Override
    protected LoginFieldDecorator createFieldDecorator(final FormFieldDecoratorOptions options) {
        return new LoginFieldDecorator(options);
    }

    @Override
    protected LoginFieldDecoratorOptions createFieldDecoratorOptions() {
        return new LoginFieldDecoratorOptions();
    }

    public class LoginFieldDecorator extends FormFieldDecorator {

        protected LoginFieldDecorator(FormFieldDecoratorOptions options) {
            super(options);
        }

        @Override
        public void init(CField<?, ?> component) {
            super.init(component);
            if (component instanceof IAcceptsWatermark && ((IAcceptsWatermark) component).getWatermark() == null) {
                ((IAcceptsWatermark) component).setWatermark(component.getTitle());
            }
        }
    }

    public class LoginFieldDecoratorOptions extends FormFieldDecoratorOptions {

        public LoginFieldDecoratorOptions() {
            super();
            labelPosition(LabelPosition.hidden);
            useLabelSemicolon(false);
            mandatoryMarker(false);
        }

    }

}
