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
import com.google.gwt.user.client.ui.HTMLPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.resident.themes.MoveInWizardTheme;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class MoveInWizardPapPreviewGadget extends AbstractGadget<MoveInWizardStepPreviewView> {

    private static final I18n i18n = I18n.get(NewTenantWelcomeGadget.class);

    public MoveInWizardPapPreviewGadget(MoveInWizardStepPreviewView view) {
        super(view, null, i18n.tr("AutoPay Setup (Pre-Authorized Payments)"), ThemeColor.contrast4, 1);
        setActionsToolbar(new ActionsToolbar());

        SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

        htmlBuilder.appendHtmlConstant("<div style='text-align:left'><div><b>");
        htmlBuilder.appendEscaped(i18n.tr("Use a SAFER and MORE SECURE method to pay your rent."));
        htmlBuilder.appendHtmlConstant("</b></div><div>");
        htmlBuilder
                .appendEscaped(i18n
                        .tr("With our pre-authorized payment option, your rent payments are much more secure, saving you both time and worry regarding cheque duplication, fraudulent bank-account access and late payment issues."));
        htmlBuilder.appendHtmlConstant("</div></div>");

        HTMLPanel htmlPanel = new HTMLPanel(htmlBuilder.toSafeHtml());
        setContent(htmlPanel);
    }

    class ActionsToolbar extends GadgetToolbar {

        public ActionsToolbar() {

            Button continueButton = new Button(i18n.tr("Setup AutoPay"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial.PreauthorizedPayments.AutoPayWizard());
                }
            });
            continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(continueButton);

            Button skipButton = new Button(i18n.tr("later"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizard());
                }
            });
            skipButton.addStyleName(MoveInWizardTheme.StyleName.DoItLaterButton.name());
            addItem(skipButton);

        }
    }

}
