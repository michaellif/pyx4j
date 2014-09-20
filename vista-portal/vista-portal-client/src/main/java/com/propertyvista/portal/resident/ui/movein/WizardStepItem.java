/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveinWizardStep;
import com.propertyvista.portal.shared.ui.MenuItem;

public class WizardStepItem extends MenuItem<StepIndexLabel> {

    public static enum StepStatus {
        notComplete, complete, current
    }

    private final MoveinWizardStep step;

    public WizardStepItem(MoveinWizardStep step, Command command, int index) {
        super(step.toString(), command, new StepIndexLabel(String.valueOf(index + 1)), ThemeColor.contrast1);
        this.step = step;
    }

    public void setStatus(StepStatus status) {
        getIcon().setStatus(status);
        super.setSelected(StepStatus.current.equals(status));
    }

    public MoveinWizardStep getStepType() {
        return step;
    }

}