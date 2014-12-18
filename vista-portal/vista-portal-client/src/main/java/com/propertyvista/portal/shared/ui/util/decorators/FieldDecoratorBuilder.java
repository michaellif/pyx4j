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
package com.propertyvista.portal.shared.ui.util.decorators;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;

public class FieldDecoratorBuilder extends FieldDecorator.Builder<FieldDecoratorBuilder> {

    // default sizes (in pixels): 
    public static final int LABEL_WIDTH = 220;

    public static final int CONTENT_WIDTH = 250;

    public FieldDecoratorBuilder(String labelWidth, String componentWidth, String contentWidth) {
        super();
        labelWidth(labelWidth);
        componentWidth(componentWidth);
    }

    public FieldDecoratorBuilder(int labelWidth, int componentWidth, int contentWidth) {
        this(labelWidth + "px", componentWidth + "px", contentWidth + "px");
    }

    public FieldDecoratorBuilder(int labelWidth, int componentWidth) {
        this(labelWidth, componentWidth, CONTENT_WIDTH);
    }

    public FieldDecoratorBuilder(int componentWidth) {
        this(LABEL_WIDTH, componentWidth);
    }

    public FieldDecoratorBuilder() {
        this(CONTENT_WIDTH);
    }

    @Override
    public FormWidgetDecorator build() {
        return new FormWidgetDecorator(this);
    }
}
