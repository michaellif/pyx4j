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

import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveinWizardStep;

public class MoveInWizardManager {

    private static Collection<MoveinWizardStep> completeSteps = new ArrayList<>();

    static {
//        markStepComplete(MoveinWizardStep.leaseSigning);
//        markStepComplete(MoveinWizardStep.pap);
//        markStepComplete(MoveinWizardStep.insurance);
    }

    public static boolean isStepComplete(MoveinWizardStep step) {
        return completeSteps.contains(step);
    }

    public static void markStepComplete(MoveinWizardStep step) {
        completeSteps.add(step);
    }

    public static MoveinWizardStep getNextStep() {
        for (MoveinWizardStep step : MoveinWizardStep.values()) {
            if (!isStepComplete(step)) {
                return step;
            }
        }
        return null;
    }

    public static Collection<MoveinWizardStep> getCompleteSteps() {
        return completeSteps;
    }

    public static MoveinWizardStep getCurrentStep() {
        return MoveinWizardStep.pap;
    }
}
