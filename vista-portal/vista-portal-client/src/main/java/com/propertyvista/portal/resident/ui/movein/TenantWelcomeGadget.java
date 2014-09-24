/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.resident.activity.movein.MoveInWizardManager;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class TenantWelcomeGadget extends AbstractGadget<MoveInWizardView> {

    private static final I18n i18n = I18n.get(TenantWelcomeGadget.class);

    public TenantWelcomeGadget(MoveInWizardView view) {
        super(view, null, i18n.tr("<b>Congratulations"), ThemeColor.contrast2, 1);
        setActionsToolbar(new NewResidentWelcomeToolbar());

        SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

        htmlBuilder.appendHtmlConstant("<div style='text-align:left'><div><b>");
        htmlBuilder.appendEscaped(i18n.tr("Great News! Your lease application has been APPROVED."));
        htmlBuilder.appendHtmlConstant("</b></div><div>");
        htmlBuilder.appendEscaped(i18n
                .tr("Getting your process finished and you to “move in ready” status can be 100% completed following our simple step-by-step online process."));
        htmlBuilder.appendHtmlConstant("</div><br><div><b>");
        htmlBuilder.appendEscaped(i18n.tr("You will need about 10 minutes to complete all the steps."));
        htmlBuilder.appendHtmlConstant("</b></div><div>");
        htmlBuilder.appendEscaped(i18n.tr("When you are ready simply click on the link below."));
        htmlBuilder.appendHtmlConstant("</div></div>");

        HTMLPanel htmlPanel = new HTMLPanel(htmlBuilder.toSafeHtml());
        setContent(htmlPanel);
    }

    class NewResidentWelcomeToolbar extends GadgetToolbar {

        private final Button startButton;

        public NewResidentWelcomeToolbar() {

            startButton = new Button(i18n.tr("I’m Ready. Let’s Go!"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.MoveIn.MoveInWizard());
                }
            });
            startButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            addItem(startButton);

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
