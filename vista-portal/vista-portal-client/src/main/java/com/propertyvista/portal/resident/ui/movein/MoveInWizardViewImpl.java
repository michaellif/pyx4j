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

import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.shared.themes.DashboardTheme;

public class MoveInWizardViewImpl extends SimplePanel implements MoveInWizardView {

    public MoveInWizardViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());
    }

    @Override
    public void showStepPreview(MoveInWizardStep step) {
        switch (step) {
        case leaseSigning:
            setWidget(new MoveInWizardLeaseSigningPreviewGadget(this));
            break;
        case pap:
            setWidget(new MoveInWizardPapPreviewGadget(this));
            break;
        case insurance:
            setWidget(new MoveInWizardInsurancePreviewGadget(this));
            break;
        }

    }

    @Override
    public void showTenantWelcomeScreen() {
        setWidget(new TenantWelcomeGadget(this));
    }

    @Override
    public void showGuarantorWelcomeScreen() {
        setWidget(new GuarantorWelcomeGadget(this));
    }

    @Override
    public void showCompletionConfirmationScreen() {
        setWidget(new MoveInWizardCompletionConfirmationGadget(this));
    }

    @Override
    public void showProgressScreen() {
        setWidget(new MoveInWizardProgressGadget(this));
    }

}
