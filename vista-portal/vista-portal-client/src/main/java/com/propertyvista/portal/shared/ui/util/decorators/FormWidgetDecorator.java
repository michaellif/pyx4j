/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 26, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.decorators;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.LabelPosition;

import com.propertyvista.portal.shared.ui.AbstractPortalPanel;

public class FormWidgetDecorator extends WidgetDecorator {

    protected FormWidgetDecorator(Builder builder) {
        super(builder);
    }

    public static class Builder extends WidgetDecorator.Builder {

        public Builder(CComponent<?> component) {
            super(component);
        }

        @Override
        public WidgetDecorator build() {
            return new FormWidgetDecorator(this) {
                @Override
                protected void onLoad() {
                    if (getLabelPosition() != LabelPosition.hidden) {
                        labelPosition(AbstractPortalPanel.getWidgetLabelPosition());
                        updateLabelPosition();
                    }
                };
            };
        }
    }

    public void setLabelPosition(LabelPosition layout) {
        getBuilder().labelPosition(layout);
        updateLabelPosition();
    }

}
