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
 */
package com.pyx4j.site.client.backoffice.ui.prime.form;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;

public class FieldDecoratorBuilder extends FieldDecorator.Builder<FieldDecoratorBuilder> {

    // default sizes (in EMs): 
    public static final double LABEL_WIDTH = 15;

    public static final double CONTENT_WIDTH = 25;

    public static final double CONTENT_WIDTH_DUAL = 65;

    public FieldDecoratorBuilder(String labelWidth, String componentWidth) {
        super();
        labelWidth(labelWidth);
        componentWidth(componentWidth);
    }

    public FieldDecoratorBuilder(double labelWidth, double componentWidth) {
        this(labelWidth + "em", componentWidth + "em");
    }

    public FieldDecoratorBuilder(double componentWidth, boolean dual) {
        this(LABEL_WIDTH, componentWidth);
    }

    public FieldDecoratorBuilder(double componentWidth) {
        this(componentWidth, false);
    }

    public FieldDecoratorBuilder(boolean dual) {
        this(dual ? CONTENT_WIDTH_DUAL : CONTENT_WIDTH, dual);
    }

    public FieldDecoratorBuilder() {
        this(false);
    }

    @Override
    public FieldDecorator build() {
        return new FieldDecorator(this) {
            @Override
            protected void updateViewable() {
                if (getLabelPosition() != LabelPosition.top) {
                    if (getComponent().isViewable()) {
                        labelAlignment(Alignment.left);
                        useLabelSemicolon(false);
                    } else {
                        labelAlignment(Alignment.right);
                        useLabelSemicolon(true);
                    }
                }
                renderLabel();
                updateLabelAlignment();
                super.updateViewable();
            }
        };
    }

}
