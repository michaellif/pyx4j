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
package com.propertyvista.common.client.ui.decorations;

import com.pyx4j.forms.client.ui.CComponent;

public class TableDecoratorBuilder extends TableWidgetDecorator.Builder {

    // default sizes (in EMs): 
    public static final double LABEL_WIDTH = 15;

    public static final double CONTENT_WIDTH = 25;

    public static final double CONTENT_WIDTH_DUAL = 65;

    public TableDecoratorBuilder(CComponent<?> component, String labelWidth, String componentWidth, String contentWidth) {
        super(component);
        labelWidth(labelWidth);
        contentWidth(contentWidth);
        componentWidth(componentWidth);

    }

    public TableDecoratorBuilder(CComponent<?> component, double labelWidth, double componentWidth, double contentWidth) {
        this(component, labelWidth + "em", componentWidth + "em", contentWidth + "em");
    }

    public TableDecoratorBuilder(CComponent<?> component, double componentWidth, boolean dual) {
        this(component, LABEL_WIDTH, componentWidth, (dual ? CONTENT_WIDTH_DUAL : CONTENT_WIDTH));
    }

    public TableDecoratorBuilder(CComponent<?> component, double componentWidth) {
        this(component, componentWidth, false);
    }

    public TableDecoratorBuilder(CComponent<?> component, boolean dual) {
        this(component, (dual ? CONTENT_WIDTH_DUAL : CONTENT_WIDTH), dual);
    }

    public TableDecoratorBuilder(CComponent<?> component) {
        this(component, false);
    }

    @Override
    public TableWidgetDecorator build() {
        return new TableWidgetDecorator(this) {
            @Override
            protected void updateViewable() {
                if (getComnponent().isViewable()) {
                    labelAlignment(Alignment.left);
                    useLabelSemicolon(false);
                } else {
                    labelAlignment(Alignment.right);
                    useLabelSemicolon(true);
                }
                updateCaption();
                updateLabelAlignment();
                super.updateViewable();
            }
        };
    }
}
