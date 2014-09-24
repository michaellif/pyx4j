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

import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.shared.ui.MenuItem;

public class MoveInWizardStepMenuItem extends MenuItem<MoveInWizardStepIndexLabel> {

    public static enum StepStatus {
        notComplete, complete, current
    }

    private final MoveInWizardStep step;

    public MoveInWizardStepMenuItem(MoveInWizardStep step, Command command, int index, ThemeColor color) {
        super(step.toString(), command, new MoveInWizardStepIndexLabel(String.valueOf(index + 1)), color);
        this.step = step;
    }

    public void setStatus(StepStatus status) {
        getIcon().setStatus(status);
        super.setSelected(StepStatus.current.equals(status));
    }

    public MoveInWizardStep getStepType() {
        return step;
    }

}