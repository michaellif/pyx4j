/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.ob.client.forms.StepStatusIndicator.StepStatus;

public class PmcAccountCreationProgressViewAltImpl extends Composite implements PmcAccountCreationProgressView {

    public static final I18n i18n = I18n.get(PmcAccountCreationProgressViewAltImpl.class);

    private final Canvas canvas;

    private int lastCompleteStep = 0;

    private List<String> stepNames;

    public PmcAccountCreationProgressViewAltImpl() {
        FlowPanel panel = new FlowPanel();
        Label caption = new Label();
        caption.setText(i18n.tr("Welcome to Property Vista"));
        panel.add(caption);

        canvas = Canvas.createIfSupported();
        panel.add(canvas);
        initWidget(panel);
    }

    @Override
    public void init(List<String> stepNames) {
        if (stepNames != null) {
            this.stepNames = stepNames;
            this.lastCompleteStep = 1;
            drawSteps(stepNames.size(), lastCompleteStep);
        }
    }

    @Override
    public void setStepStatus(String stepName, StepStatus status) {
        for (int i = 0; i < this.stepNames.size(); ++i) {
            if (stepName.equals(stepNames.get(i)) && status == StepStatus.COMPLETE) {
                lastCompleteStep = i + 1;
            }
        }
        drawSteps(stepNames.size(), lastCompleteStep);

    }

    @Override
    public void setCrmSiteUrl(String url) {
        // TODO Auto-generated method stub

    }

    @Override
    public IMemento getMemento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storeState(Place place) {
        // TODO Auto-generated method stub

    }

    @Override
    public void restoreState() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showVisor(IsWidget widget, String caption) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }

    private void drawSteps(final int numOfSteps, int completeStep) {
        final int width = 700;
        final int height = 50;
        final int radius = height / 2;
        final int space = Math.min(20, (width - numOfSteps * radius * 2) / (numOfSteps - 1));
        final int startX = 0;
        final int connectorHalfHeight = 5;
        CssColor completeColor = CssColor.make(0x55, 0xAA, 0x00);
        CssColor incompleteColor = CssColor.make(0xAA, 0xAA, 0xAA);
        canvas.setWidth("" + width + "px");
        canvas.setCoordinateSpaceWidth(width);
        canvas.setHeight("" + height + "px");
        canvas.setCoordinateSpaceHeight(height);

        Context2d context = canvas.getCanvasElement().getContext2d();

        for (int step = 1; step <= numOfSteps - 1; ++step) {
            context.setFillStyle(step < completeStep - 1 ? completeColor : incompleteColor);
            context.beginPath();
            context.rect(startX + step * radius + (step - 1) * (space + radius), height / 2 - connectorHalfHeight, radius * 2, 2 * connectorHalfHeight);
            context.closePath();
            context.fill();
        }

        for (int step = 1; step <= numOfSteps; ++step) {
            context.setFillStyle(step < completeStep ? completeColor : incompleteColor);
            context.beginPath();
            context.arc(startX + step * radius + (step - 1) * (space + radius), height / 2, radius, 0, Math.PI * 2, true);
            context.closePath();
            context.fill();
        }

    }
}
