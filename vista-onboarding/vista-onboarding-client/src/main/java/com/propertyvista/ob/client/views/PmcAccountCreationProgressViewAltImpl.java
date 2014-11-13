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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.visor.IVisor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.ob.client.forms.StepStatusIndicator.StepStatus;
import com.propertyvista.ob.client.themes.OnboardingStyles;
import com.propertyvista.ob.client.themes.OnboardingTheme;
import com.propertyvista.ob.client.views.PmcAccountCreationCompleteViewImpl.PmcSiteRedirectPanel;
import com.propertyvista.ob.rpc.dto.OnboardingCrmURL;

public class PmcAccountCreationProgressViewAltImpl extends Composite implements PmcAccountCreationProgressView {

    public enum Styles implements IStyleName {
        PleaseWaitLabel, ProgressStepDetails;
    }

    public static class ProgressCircles extends Composite {

        private final Canvas canvas;

        private final Label label;

        private int numOfSteps;

        private final int width;

        private final int height;

        private final CssColor incompleteColor;

        private final CssColor completeColor;

        public ProgressCircles(int width, int height, int numOfSteps, CssColor incompleteColor, CssColor completeColor) {
            FlowPanel panel = new FlowPanel();
            this.numOfSteps = numOfSteps;
            this.width = width;
            this.height = height;
            this.incompleteColor = incompleteColor;
            this.completeColor = completeColor;

            canvas = Canvas.createIfSupported();
            canvas.setWidth("" + width + "px");
            canvas.setCoordinateSpaceWidth(width);
            canvas.setHeight("" + height + "px");
            canvas.setCoordinateSpaceHeight(height);

            if (canvas != null) {
                label = null;
                panel.add(canvas);
            } else {
                label = new Label();
                label.setText(i18n.tr("Step 1 of {0}", numOfSteps));
                panel.add(label);
            }

            setCompleteStep(0);
            initWidget(panel);
        }

        /** sets number of steps reset the complete steps */
        public void setNumOfSteps(int numOfSteps) {
            this.numOfSteps = numOfSteps;
            setCompleteStep(0);
        }

        public void setCompleteStep(int completeStep) {
            if (canvas != null) {

                final int radius = height / 2;
                final int space = Math.min(20, (width - numOfSteps * radius * 2) / (numOfSteps - 1));
                // calc the start offset to put all the circles in center
                final int startX = width / 2 - ((radius * numOfSteps) + space * (numOfSteps - 1) / 2);
                final int connectorHalfHeight = 3;

                Context2d context = canvas.getCanvasElement().getContext2d();
                context.clearRect(0, 0, width, height);

                // draw connecting sticks
                for (int step = 1; step <= numOfSteps - 1; ++step) {
                    context.setFillStyle(step < completeStep ? completeColor : incompleteColor);
                    context.beginPath();
                    context.rect(startX + step * radius + (step - 1) * (space + radius), height / 2 - connectorHalfHeight, radius * 2, 2 * connectorHalfHeight);
                    context.closePath();
                    context.fill();
                }

                // draw circles
                for (int step = 1; step <= numOfSteps; ++step) {
                    context.setFillStyle(step <= completeStep ? completeColor : incompleteColor);
                    context.beginPath();
                    context.arc(startX + step * radius + (step - 1) * (space + radius), height / 2, radius, 0, Math.PI * 2, true);
                    context.closePath();
                    context.fill();
                }

            } else {
                label.setText(i18n.tr("Step {0} of {1}", completeStep, numOfSteps));
            }

        }

    }

    public static final I18n i18n = I18n.get(PmcAccountCreationProgressViewAltImpl.class);

    private int lastCompleteStep = 0;

    private List<String> stepNames;

    private final ProgressCircles circles;

    private final Label currentStepDetails;

    private final PmcSiteRedirectPanel pmcCreatedPanel;

    private final Label pleaseWait;

    public PmcAccountCreationProgressViewAltImpl() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(OnboardingStyles.VistaObView.name());
        Label caption = new Label();
        caption.addStyleName(OnboardingStyles.OnboardingCaption.name());
        caption.setText(i18n.tr("Welcome to PropertyVista"));
        panel.add(caption);

        pleaseWait = new Label();
        pleaseWait.addStyleName(Styles.PleaseWaitLabel.name());
        panel.add(pleaseWait);

        circles = new ProgressCircles(700, 50, 10, OnboardingTheme.COLOR_PROGRESS_CIRCLE_INCOMPLETE, OnboardingTheme.COLOR_PROGRESS_CIRCLE_COMPLETE);
        circles.getElement().getStyle().setPaddingTop(1, Unit.EM);
        circles.getElement().getStyle().setPaddingBottom(1, Unit.EM);
        panel.add(circles);

        currentStepDetails = new Label();
        currentStepDetails.addStyleName(Styles.ProgressStepDetails.name());
        panel.add(currentStepDetails);

        pmcCreatedPanel = new PmcAccountCreationCompleteViewImpl.PmcSiteRedirectPanel();
        pmcCreatedPanel.getElement().getStyle().setMarginTop(30, Unit.PX);
        pmcCreatedPanel.setHeight("100px");
        panel.add(pmcCreatedPanel);
        initWidget(panel);
    }

    @Override
    public void init(List<String> stepNames) {
        this.pleaseWait.setText(i18n.tr("Please wait... it can take up to 30 secs."));
        this.pmcCreatedPanel.setCrmSiteUrl(null);
        if (stepNames != null) {
            this.stepNames = stepNames;
            this.lastCompleteStep = 0;
            this.circles.setNumOfSteps(stepNames.size());
            this.currentStepDetails.setText(stepNames.get(lastCompleteStep));
        } else {
            this.stepNames = null;
            this.lastCompleteStep = -1;
            this.circles.setVisible(false);
            this.currentStepDetails.setText("");
        }
    }

    @Override
    public void setStepStatus(String stepName, StepStatus status) {
        for (int i = 0; i < this.stepNames.size(); ++i) {
            if (stepName.equals(stepNames.get(i)) && status == StepStatus.COMPLETE) {
                this.lastCompleteStep = i + 1;
                this.circles.setCompleteStep(lastCompleteStep);
                this.currentStepDetails.setText(stepNames.get(lastCompleteStep - 1));
            }
        }
    }

    @Override
    public void setCrmSiteUrl(OnboardingCrmURL url) {
        pleaseWait.setHTML("&nbsp;");
        pmcCreatedPanel.setCrmSiteUrl(url);
    }

    @Override
    public void showVisor(IVisor visor) {
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

    @Override
    public void setPresenter(IPanePresenter presenter) {
        // TODO Auto-generated method stub

    }

    @Override
    public IPanePresenter getPresenter() {
        // TODO Auto-generated method stub
        return null;
    }
}
