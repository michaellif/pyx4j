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
 */
package com.propertyvista.portal.shared.ui.util.decorators;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;

public class FormWidgetDecorator extends FieldDecorator {

    private final FieldDecoratorBuilder builder;

    protected FormWidgetDecorator(FieldDecoratorBuilder builder) {
        super(builder);
        this.builder = builder;
    }

    @Override
    protected void updateViewable() {
        if (getLabelPosition() != LabelPosition.top) {
            if (getComponent().isViewable()) {
                builder.labelAlignment(Alignment.left);
                builder.useLabelSemicolon(false);
            } else {
                builder.labelAlignment(Alignment.right);
                builder.useLabelSemicolon(true);
            }
        }
        renderLabel();
        updateLabelAlignment();
        super.updateViewable();
    }

}
