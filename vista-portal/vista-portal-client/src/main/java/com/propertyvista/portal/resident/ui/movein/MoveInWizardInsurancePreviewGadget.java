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
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class MoveInWizardInsurancePreviewGadget extends AbstractGadget<MoveInWizardStepPreviewView> {

    private static final I18n i18n = I18n.get(NewTenantWelcomeGadget.class);

    public MoveInWizardInsurancePreviewGadget(MoveInWizardStepPreviewView view) {
        super(view, null, i18n.tr("Get Insurane"), ThemeColor.contrast2, 1);
        setActionsToolbar(new MoveInWizardLeaseSigningPreviewToolbar());

        setContent(new HTML("Insurane Explanantion"));
    }

    class MoveInWizardLeaseSigningPreviewToolbar extends GadgetToolbar {

        public MoveInWizardLeaseSigningPreviewToolbar() {

            Button continueButton = new Button(i18n.tr("Buy Insurane"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizard());
                }
            });
            continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            addItem(continueButton);

            Button skipButton = new Button(i18n.tr("I will do it later"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizard());
                }
            });
            skipButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 0.7));
            addItem(skipButton);

        }
    }

    public void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            break;

        default:
            break;
        }
    }
}
