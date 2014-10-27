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

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class MoveInWizardLeaseSigningPreviewGadget extends AbstractGadget<MoveInWizardView> {

    private static final I18n i18n = I18n.get(MoveInWizardLeaseSigningPreviewGadget.class);

    public MoveInWizardLeaseSigningPreviewGadget(MoveInWizardView view) {
        super(view, null, i18n.tr("Lease Agreement "), ThemeColor.contrast2, 1);
        setActionsToolbar(new ActionsToolbar());

        SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();

        htmlBuilder.appendHtmlConstant("<div style='text-align:left'><div><b>");
        htmlBuilder.appendEscaped(i18n.tr("Sign your Lease Agreement from the convenience of your computer."));
        htmlBuilder.appendHtmlConstant("</b></div><div>");
        htmlBuilder
                .appendEscaped(i18n
                        .tr("Our online signing process is fully secure and meets all the necessary legal requirements.  A copy of your completed lease for your own records will be also emailed to you once completed."));
        htmlBuilder.appendHtmlConstant("</div></div>");

        HTMLPanel htmlPanel = new HTMLPanel(htmlBuilder.toSafeHtml());
        setContent(htmlPanel);
    }

    class ActionsToolbar extends GadgetToolbar {

        private final Button startButton;

        public ActionsToolbar() {

            startButton = new Button(i18n.tr("Sign Your Lease"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizard());
                }
            });
            startButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            addItem(startButton);

        }
    }

}
