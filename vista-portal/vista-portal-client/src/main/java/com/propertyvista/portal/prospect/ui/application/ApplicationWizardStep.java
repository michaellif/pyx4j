/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;

public abstract class ApplicationWizardStep extends WizardStep {

    private ApplicationWizard wizard;

    public ApplicationWizardStep() {
        setStepContent(createStepContent());
    }

    public void setWizard(ApplicationWizard wizard) {
        this.wizard = wizard;
    }

    public ApplicationWizard getWizard() {
        return wizard;
    }

    abstract public BasicFlexFormPanel createStepContent();

    public void addValidations() {
    }

    public void onValueSet() {
    }
}