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
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder;

/** Sets up defaults for Landing Forms (Login, Password Reset, TenantRegsitration etc.. ), right now intended to be used not with 'editable' forms only */
public class LoginDecoratorBuilder extends WidgetDecorator.Builder {

    private String watermark;

    private final boolean useWatermark;

    public LoginDecoratorBuilder(CComponent<?, ?> component, boolean useWatermark) {
        super(component);
        this.useWatermark = useWatermark;
    }

    public Builder watermark(String watermark) {
        this.watermark = watermark;
        return this;
    }

    @Override
    public WidgetDecorator build() {
        if (useWatermark) {
            customLabel("");
            labelWidth(0);
            useLabelSemicolon(false);
            if (getComponent() instanceof CTextFieldBase) {
                ((CTextFieldBase<?, ?>) getComponent()).setWatermark(watermark != null ? watermark : getComponent().getTitle());
            }
        } else {
            layout(Layout.vertical);
            labelAlignment(Alignment.left);
            useLabelSemicolon(true);
        }
        mandatoryMarker(false);
        componentWidth(20);

        return super.build();
    }

}
