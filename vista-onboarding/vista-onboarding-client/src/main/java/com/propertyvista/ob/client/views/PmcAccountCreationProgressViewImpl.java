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
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.ui.crud.misc.IMemento;

import com.propertyvista.ob.client.forms.StepStatusIndicator;
import com.propertyvista.ob.client.forms.StepStatusIndicator.StepStatus;

public class PmcAccountCreationProgressViewImpl extends Composite implements PmcAccountCreationProgressView {

    private final List<StepStatusIndicator> stepStatuses;

    private final FlowPanel stepsPanel;

    public PmcAccountCreationProgressViewImpl() {
        this.stepStatuses = new ArrayList<StepStatusIndicator>();
        this.stepsPanel = new FlowPanel();
        this.stepsPanel.setWidth("40em");
        this.stepsPanel.getElement().getStyle().setProperty("marginLeft", "auto");
        this.stepsPanel.getElement().getStyle().setProperty("marginRight", "auto");
        initWidget(this.stepsPanel);
    }

    @Override
    public void init(List<String> stepNames) {
        stepStatuses.clear();
        stepsPanel.clear();
        for (String stepName : stepNames) {
            StepStatusIndicator stepStatus = new StepStatusIndicator(stepName);
            stepsPanel.add(stepStatus);
            stepStatuses.add(stepStatus);
        }
    }

    @Override
    public void setStatus(String stepName, StepStatus status) {
        for (StepStatusIndicator stepStatus : stepStatuses) {
            if (stepStatus.getName().equals(stepName)) {
                stepStatus.setStatus(status);
                break;
            }
        }
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

}
