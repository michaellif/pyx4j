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

import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder;

/** Sets up defaults for Landing Forms (Login, Password Reset, TenantRegsitration etc.. ), right now intended to be used not with 'editable' forms only */
public class LoginDecoratorBuilder extends WidgetDecorator.Builder {

    private String watermark;

    public LoginDecoratorBuilder(CComponent<?> component) {
        super(component);
        customLabel("");
        labelWidth("0");
        useLabelSemicolon(false);
        mandatoryMarker(false);
        componentWidth("100%");
        contentWidth("280px");
    }

    public Builder watermark(String watermark) {
        this.watermark = watermark;
        return this;
    }

    @Override
    public WidgetDecorator build() {
        String text = watermark != null ? watermark : getComponent().getTitle();
        if (getComponent() instanceof CTextFieldBase) {
            ((CTextFieldBase<?, ?>) getComponent()).setWatermark(text);
        } else if (getComponent() instanceof CCaptcha) {
            ((CCaptcha) getComponent()).setWatermark(text);
        }
        return super.build();
    }
}
