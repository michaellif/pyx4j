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

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.resident.activity.movein.MoveInWizardManager;
import com.propertyvista.portal.resident.themes.MoveInWizardTheme;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class MoveInWizardInsurancePreviewGadget extends AbstractGadget<MoveInWizardView> {

    private static final I18n i18n = I18n.get(TenantWelcomeGadget.class);

    public MoveInWizardInsurancePreviewGadget(MoveInWizardView view) {
        super(view, null, i18n.tr("Renters Insurance"), ThemeColor.contrast3, 1);
        setActionsToolbar(new MoveInWizardLeaseSigningPreviewToolbar());

        SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

        htmlBuilder.appendHtmlConstant("<div style='text-align:left'><div><b>");
        htmlBuilder.appendEscaped(i18n.tr("Don’t kick yourself for not being PREPARED."));
        htmlBuilder.appendHtmlConstant("</b></div><div>");
        htmlBuilder
                .appendEscaped(i18n
                        .tr("Unexpected accidents can happen, like burst pipes, fires and thefts. Just because you rent, doesn’t mean you don’t have responsibilities. You could lose a lot of money and you could be held liable for damages you may cause both inside and outside your unit."));
        htmlBuilder.appendHtmlConstant("</div><br><div>");
        htmlBuilder.appendEscaped(i18n
                .tr("Not to mention, to be fully lease compliant, you must have renters insurance in place before you can receive a key to your unit."));
        htmlBuilder.appendHtmlConstant("</div><br><div><b>");

        htmlBuilder.appendEscaped(i18n.tr("We have a SOLUTION."));
        htmlBuilder.appendHtmlConstant("</b></div><div>");

        htmlBuilder
                .appendEscaped(i18n
                        .tr("To help everyone in the building with the points mentioned above, we have partnered with TenantSure across all our properties to provide tenant insurance at group discount rates.  Signing up is easy, and can be 100% completed and approved online."));
        htmlBuilder.appendHtmlConstant("</div></div>");

        HTMLPanel htmlPanel = new HTMLPanel(htmlBuilder.toSafeHtml());
        setContent(htmlPanel);
    }

    class MoveInWizardLeaseSigningPreviewToolbar extends GadgetToolbar {

        public MoveInWizardLeaseSigningPreviewToolbar() {

            Button continueButton = new Button(i18n.tr("Buy Insurance"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSureWizard());
                }
            });
            continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            addItem(continueButton);

            Button skipButton = new Button(i18n.tr("later"), new Command() {
                @Override
                public void execute() {
                    MoveInWizardManager.skipStep(MoveInWizardStep.insurance, new AsyncCallback<VoidSerializable>() {

                        @Override
                        public void onSuccess(VoidSerializable result) {
                            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.MoveIn.MoveInWizard());
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.MoveIn.MoveInWizard());
                        }
                    });
                }
            });
            skipButton.addStyleName(MoveInWizardTheme.StyleName.DoItLaterButton.name());
            addItem(skipButton);
        }
    }

}
