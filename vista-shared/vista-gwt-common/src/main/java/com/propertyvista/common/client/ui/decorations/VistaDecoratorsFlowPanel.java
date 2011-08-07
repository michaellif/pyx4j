/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 3, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.decorations;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.decorations.DecorationData.ShowMandatory;

public class VistaDecoratorsFlowPanel extends FlowPanel {

    public static final String DEFAULT_STYLE_NAME = "vista_DecoratorsFlowPanel";

    private boolean readOnlyMode = false;

    private double defaultLabelWidth = 12;

    private boolean showMandatory = true;

    public VistaDecoratorsFlowPanel() {
        setStyleName(DEFAULT_STYLE_NAME);
    }

    public VistaDecoratorsFlowPanel(double defaultLabelWidth) {
        this();
        this.defaultLabelWidth = defaultLabelWidth;
    }

    public VistaDecoratorsFlowPanel(boolean readOnlyMode) {
        this();
        this.readOnlyMode = readOnlyMode;
    }

    public VistaDecoratorsFlowPanel(boolean readOnlyMode, double defaultLabelWidth) {
        this();
        this.readOnlyMode = readOnlyMode;
        this.defaultLabelWidth = defaultLabelWidth;
    }

    public void add(final CComponent<?> component, double componentWidth) {
        this.add(component, defaultLabelWidth, componentWidth);
    }

    public void add(final CComponent<?> component, double componentWidth, String componentCaption) {
        this.add(component, defaultLabelWidth, componentWidth, componentCaption);
    }

    public void add(final CComponent<?> component, double labelWidth, double componentWidth) {
        this.add(component, labelWidth, componentWidth, null);
    }

    public void add(final CComponent<?> component, double labelWidth, double componentWidth, String componentCaption) {
        super.add(createDecorator(component, labelWidth, componentWidth, componentCaption));
        if (!readOnlyMode) {
            super.add(new HTML());
        }
    }

    public VistaWidgetDecorator createDecorator(final CComponent<?> component, double componentWidth) {
        return this.createDecorator(component, defaultLabelWidth, componentWidth);
    }

    public VistaWidgetDecorator createDecorator(final CComponent<?> component, double labelWidth, double componentWidth) {
        return this.createDecorator(component, labelWidth, componentWidth, null);
    }

    public VistaWidgetDecorator createDecorator(final CComponent<?> component, double labelWidth, double componentWidth, String componentCaption) {
        DecorationData decorData = new DecorationData();
        decorData.componentWidth = componentWidth;
        decorData.componentCaption = componentCaption;
        decorData.labelWidth = labelWidth;
        decorData.readOnlyMode = readOnlyMode;
        if (!showMandatory) {
            decorData.showMandatory = ShowMandatory.None;
        }
        if (readOnlyMode) {
            decorData.labelAlignment = HasHorizontalAlignment.ALIGN_LEFT;
        }
        return new VistaWidgetDecorator(component, decorData);
    }

    public double getDefaultLabelWidth() {
        return defaultLabelWidth;
    }

    public void setDefaultLabelWidth(double defaultLabelWidth) {
        this.defaultLabelWidth = defaultLabelWidth;
    }

    public boolean isShowMandatory() {
        return showMandatory;
    }

    public void setShowMandatory(boolean showMandatory) {
        this.showMandatory = showMandatory;
    }

    public boolean isReadOnlyMode() {
        return readOnlyMode;
    }

    public void setReadOnlyMode(boolean readOnlyMode) {
        this.readOnlyMode = readOnlyMode;
    }
}
