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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.site.client.ui.visor.IVisor;

import com.propertyvista.ob.client.forms.StepStatusIndicator;
import com.propertyvista.ob.client.forms.StepStatusIndicator.StepStatus;
import com.propertyvista.ob.client.views.PmcAccountCreationCompleteViewImpl.PmcSiteRedirectPanel;
import com.propertyvista.ob.rpc.dto.OnboardingCrmURL;

public class PmcAccountCreationProgressViewImpl extends Composite implements PmcAccountCreationProgressView {

    private final List<StepStatusIndicator> stepStatuses;

    private final FlowPanel viewPanel;

    private final FlowPanel stepsPanel;

    private final PmcSiteRedirectPanel pmcCreatedPanel;

    public PmcAccountCreationProgressViewImpl() {
        this.stepStatuses = new ArrayList<StepStatusIndicator>();

        this.viewPanel = new FlowPanel();

        this.stepsPanel = new FlowPanel();
        this.stepsPanel.getElement().getStyle().setProperty("marginLeft", "auto");
        this.stepsPanel.getElement().getStyle().setProperty("marginRight", "auto");
        this.stepsPanel.getElement().getStyle().setProperty("width", "400px");
        this.viewPanel.add(stepsPanel);

        this.pmcCreatedPanel = new PmcAccountCreationCompleteViewImpl.PmcSiteRedirectPanel();
        this.pmcCreatedPanel.getElement().getStyle().setMarginTop(30, Unit.PX);
        this.pmcCreatedPanel.setHeight("100px");
        this.viewPanel.add(pmcCreatedPanel);

        initWidget(this.viewPanel);
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
        setCrmSiteUrl(null);
    }

    @Override
    public void setStepStatus(String stepName, StepStatus status) {
        for (StepStatusIndicator stepStatus : stepStatuses) {
            if (stepStatus.getName().equals(stepName)) {
                stepStatus.setStatus(status);
                break;
            }
        }
    }

    @Override
    public void setCrmSiteUrl(OnboardingCrmURL url) {
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
