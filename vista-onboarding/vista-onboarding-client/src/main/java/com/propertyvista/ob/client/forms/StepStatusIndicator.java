/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author ArtyomB
 */
package com.propertyvista.ob.client.forms;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.widgets.client.Label;

public class StepStatusIndicator extends Composite {

    public enum Styles implements IStyleName {

        StepStatusIndicatorPanel;

    }

    public enum StepStatus {

        COMPLETE, INCOMPLETE, INPROGRESS;
    }

    private Image complete;

    private Image inProgress;

    private StepStatusIndicator.StepStatus status;

    private final String stepName;

    public StepStatusIndicator(String stepName) {
        this.stepName = stepName;
        FlowPanel stepStatusPanel = new FlowPanel();
        stepStatusPanel.addStyleName(Styles.StepStatusIndicatorPanel.name());
        stepStatusPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        stepStatusPanel.getElement().getStyle().setProperty("width", "100%");

        SimplePanel stepNameLabelHolder = new SimplePanel();
        stepNameLabelHolder.getElement().getStyle().setFloat(Float.LEFT);

        Label stepNameLabel = new Label();
        stepNameLabel.setText(stepName);
        stepNameLabel.getElement().getStyle().setProperty("display", "table-cell");
        stepNameLabel.getElement().getStyle().setProperty("verticalAlign", "middle");
        stepNameLabelHolder.setWidget(stepNameLabel);

        stepStatusPanel.add(stepNameLabelHolder);

        stepStatusPanel.add(complete = new Image(StepStatusResources.INSTANCE.complete()));
        complete.setVisible(false);
        complete.getElement().getStyle().setFloat(Float.RIGHT);

        stepStatusPanel.add(inProgress = new Image(StepStatusResources.INSTANCE.inProgress()));
        inProgress.setVisible(false);
        inProgress.getElement().getStyle().setFloat(Float.RIGHT);

        stepNameLabel.setHeight("" + complete.getHeight() + "px");

        setStatus(StepStatus.INCOMPLETE);
        initWidget(stepStatusPanel);
    }

    public void setStatus(StepStatusIndicator.StepStatus value) {
        this.status = value;
        complete.setVisible(value == StepStatus.COMPLETE);
        inProgress.setVisible(value == StepStatus.INPROGRESS);
    }

    public StepStatusIndicator.StepStatus getStatus() {
        return this.status;
    }

    public String getName() {
        return stepName;
    }
}