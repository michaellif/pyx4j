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
package com.propertyvista.portal.resident.activity.movein;

import java.util.ArrayList;
import java.util.Collection;

public class MoveInWizardManager {

    public enum MoveinWizardStep {
        leaseSigning, pap, insurance, profile
    }

    private static Collection<MoveinWizardStep> completedSteps = new ArrayList<>();

    public static boolean isStepComplete(MoveinWizardStep step) {
        return completedSteps.contains(step);
    }

    public static void markStepComplete(MoveinWizardStep step) {
        completedSteps.add(step);
    }

    public static MoveinWizardStep getNextStep() {
        for (MoveinWizardStep step : MoveinWizardStep.values()) {
            if (!isStepComplete(step)) {
                return step;
            }
        }
        return null;
    }
}
