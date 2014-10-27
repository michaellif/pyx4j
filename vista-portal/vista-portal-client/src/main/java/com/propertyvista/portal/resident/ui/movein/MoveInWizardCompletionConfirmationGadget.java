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

    private static final I18n i18n = I18n.get(MoveInWizardCompletionConfirmationGadget.class);

    public MoveInWizardCompletionConfirmationGadget(MoveInWizardView view) {
        super(view, null, i18n.tr("Welcome to myCommunity!"), ThemeColor.contrast4, 1);
        setActionsToolbar(new ActionsToolbar());

        setContent(new HTML(""));

        SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

        htmlBuilder.appendHtmlConstant("<div style='text-align:left'><div>");
        htmlBuilder
                .appendEscaped(i18n
                        .tr("Please familiarize yourself with the myCOMMUNITY online resident portal by selecting the link below. This online resource should be used for paying rent, submitting maintenance requests, purchasing tenant insurance and for participating in the buildings perks program. You can also update your profile information at any time directly from myCOMMUNITY."));
        htmlBuilder.appendHtmlConstant("</div></div>");

        HTMLPanel htmlPanel = new HTMLPanel(htmlBuilder.toSafeHtml());
        setContent(htmlPanel);
    }

    class ActionsToolbar extends GadgetToolbar {

        public ActionsToolbar() {

            Button continueButton = new Button(i18n.tr("myCOMMUNITY"), new Command() {
                @Override
                public void execute() {
                    MoveInWizardManager.reset();
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Dashboard());
                }
            });
            continueButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(continueButton);
        }
    }

}
