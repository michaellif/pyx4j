/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveinWizardStep;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class MoveInWizardStepPreviewViewImpl extends SimplePanel implements MoveInWizardStepPreviewView {

    private final Map<MoveinWizardStep, AbstractGadget<?>> gadgets;

    private AbstractGadget<?> currentGadget;

    public MoveInWizardStepPreviewViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        gadgets = new HashMap<>();

        AbstractGadget<?> gadget = new MoveInWizardLeaseSigningPreviewGadget(this);
        gadgets.put(MoveinWizardStep.leaseSigning, gadget);

        gadget = new MoveInWizardPapPreviewGadget(this);
        gadgets.put(MoveinWizardStep.pap, gadget);

        gadget = new MoveInWizardInsurancePreviewGadget(this);
        gadgets.put(MoveinWizardStep.insurance, gadget);

    }

    @Override
    public void setCurrentStep(MoveinWizardStep step) {
        currentGadget = gadgets.get(step);
        setWidget(currentGadget);
    }

}
