/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident;

import java.util.ArrayList;
import java.util.Collection;

import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;

public class MoveInWizardManager {

    private static Collection<MoveInWizardStep> completeSteps = new ArrayList<>();

    private static MoveInWizardStep currentStep = MoveInWizardStep.leaseSigning;

    static {
//        markStepComplete(MoveinWizardStep.leaseSigning);
//        markStepComplete(MoveinWizardStep.pap);
//        markStepComplete(MoveinWizardStep.insurance);
    }

    public static boolean isStepComplete(MoveInWizardStep step) {
        return completeSteps.contains(step);
    }

    public static void markStepComplete(MoveInWizardStep step) {
        completeSteps.add(step);
    }

    public static MoveInWizardStep getNextStep() {
        for (MoveInWizardStep step : MoveInWizardStep.values()) {
            if (!isStepComplete(step)) {
                return step;
            }
        }
        return null;
    }

    public static Collection<MoveInWizardStep> getCompleteSteps() {
        return completeSteps;
    }

    public static MoveInWizardStep getCurrentStep() {
        return currentStep;
    }

    public static void setCurrentStep(MoveInWizardStep currentStep) {
        if (!getCompleteSteps().contains(currentStep)) {
            MoveInWizardManager.currentStep = currentStep;
        }
    }
}
