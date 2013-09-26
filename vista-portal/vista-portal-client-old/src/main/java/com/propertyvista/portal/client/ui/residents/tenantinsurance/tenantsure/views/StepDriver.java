/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.client.themes.TenantSureTheme;

public class StepDriver extends Composite {

    private final List<Step> steps;

    private int currentStep = 0;

    private final Button nextStepButton;

    private final Anchor cancelButton;

    public StepDriver(List<Step> steps, final Command cancelCommand) {
        FlowPanel stepsPanel = new FlowPanel();
        this.steps = steps;
        for (Step step : steps) {
            stepsPanel.add(step);
        }

        FlowPanel buttonsPanel = new FlowPanel();
        buttonsPanel.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewSection.name());
        buttonsPanel.getElement().getStyle().setPaddingBottom(30, Unit.PX);
        cancelButton = new Anchor(TenantSurePurchaseViewImpl.i18n.tr("Cancel"));
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancelCommand.execute();
            }
        });
        cancelButton.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewCancelButton.name());
        buttonsPanel.add(cancelButton);

        nextStepButton = new Button(TenantSurePurchaseViewImpl.i18n.tr("Buy TenantSure"), new Command() {
            @Override
            public void execute() {
                StepDriver.this.steps.get(currentStep).onProceedToNext(new AsyncCallback<VoidSerializable>() {

                    @Override
                    public void onSuccess(VoidSerializable result) {
                        activateStep(currentStep + 1);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO display error
                    }

                });
            }
        });
        nextStepButton.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewNextStepButton.name());
        buttonsPanel.add(nextStepButton);

        stepsPanel.add(buttonsPanel);
        initWidget(stepsPanel);
        reset();
    }

    public void reset() {
        for (Step step : steps) {
            step.reset();
        }
        activateStep(0);
    }

    private void activateStep(int stepNumber) {
        currentStep = stepNumber;
        for (Step step : steps) {
            step.asWidget().setVisible(false);
        }
        steps.get(stepNumber).asWidget().setVisible(true);
        steps.get(stepNumber).setNextButton(nextStepButton);
        // TODO maybe setNextStepButton should set text text label??
        if (stepNumber < steps.size() - 1) {
            nextStepButton.setTextLabel(steps.get(stepNumber + 1).getTitle());
            cancelButton.setVisible(true);
            nextStepButton.setVisible(true);
        } else {
            nextStepButton.setVisible(false);
            cancelButton.setVisible(false);
        }
    }

}