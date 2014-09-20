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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.shared.themes.DashboardTheme;

public class MoveInWizardStepPreviewViewImpl extends FlowPanel implements MoveInWizardStepPreviewView {

    private final MoveInWizardLeaseSigningPreviewGadget leaseSigningPreviewGadget;

    private final MoveInWizardPapPreviewGadget papPreviewGadget;

    private final MoveInWizardInsurancePreviewGadget insurancePreviewGadget;

    private final MoveInWizardProfilePreviewGadget profilePreviewGadget;

    public MoveInWizardStepPreviewViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        leaseSigningPreviewGadget = new MoveInWizardLeaseSigningPreviewGadget(this);
        add(leaseSigningPreviewGadget);

        papPreviewGadget = new MoveInWizardPapPreviewGadget(this);
        add(papPreviewGadget);

        insurancePreviewGadget = new MoveInWizardInsurancePreviewGadget(this);
        add(insurancePreviewGadget);

        profilePreviewGadget = new MoveInWizardProfilePreviewGadget(this);
        add(profilePreviewGadget);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
        leaseSigningPreviewGadget.doLayout(layoutType);
        papPreviewGadget.doLayout(layoutType);
        insurancePreviewGadget.doLayout(layoutType);
    }
}
