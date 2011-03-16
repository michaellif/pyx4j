/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp;

import com.propertyvista.portal.domain.pt.ApplicationWizardStep;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class WizardStep {

    private final AppPlace place;

    private final ApplicationWizardStep step;

    WizardStep(ApplicationWizardStep step) {
        super();
        this.step = step;
        this.place = AppSite.instance().getHistoryMapper().getPlace(step.placeToken().getValue());
    }

    public AppPlace getPlace() {
        return place;
    }

    public ApplicationWizardStep.Status getStatus() {
        return step.status().getValue();
    }

    void setStatus(ApplicationWizardStep.Status status) {
        this.step.status().setValue(status);
    }

    public ApplicationWizardStep getStep() {
        return step;
    }
}
