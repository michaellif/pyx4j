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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.resident.activity.movein.MoveInWizardManager;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class MoveInWizardCompletionConfirmationGadget extends AbstractGadget<MoveInWizardView> {

    private static final I18n i18n = I18n.get(TenantWelcomeGadget.class);

    public MoveInWizardCompletionConfirmationGadget(MoveInWizardView view) {
        super(view, null, i18n.tr("Move-In Wizard Complete"), ThemeColor.contrast4, 1);
        setActionsToolbar(new ActionsToolbar());

        setContent(new HTML(""));

        SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

        htmlBuilder.appendHtmlConstant("<div style='text-align:left'><div><b>");
        htmlBuilder.appendEscaped(i18n.tr("Congratulations! You're MOVE IN READY!"));
        htmlBuilder.appendHtmlConstant("</b></div><div>");
        htmlBuilder.appendEscaped(i18n.tr("To continue to portal click on the \"Continue to Portal\" button or select \"Update Profile\" for changes."));
        htmlBuilder.appendHtmlConstant("</div></div>");

        HTMLPanel htmlPanel = new HTMLPanel(htmlBuilder.toSafeHtml());
        setContent(htmlPanel);
    }

    class ActionsToolbar extends GadgetToolbar {

        public ActionsToolbar() {

            Button continueButton = new Button(i18n.tr("Continue to Portal"), new Command() {
                @Override
                public void execute() {
                    MoveInWizardManager.reset();
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Dashboard());
                }
            });
            continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(continueButton);

            Button updateProfileButton = new Button(i18n.tr("Update Profile"), new Command() {
                @Override
                public void execute() {
                    MoveInWizardManager.reset();
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Profile());
                }
            });
            updateProfileButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(updateProfileButton);

        }
    }

}
