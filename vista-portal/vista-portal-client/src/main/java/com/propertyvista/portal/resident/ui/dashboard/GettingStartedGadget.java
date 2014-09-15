/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.resident.ui.PointerLink;
import com.propertyvista.portal.resident.ui.ResidentPortalPointerId;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.shared.config.VistaFeatures;

public class GettingStartedGadget extends AbstractGadget<MainDashboardViewImpl> {

    private static final I18n i18n = I18n.get(GettingStartedGadget.class);

    GettingStartedGadget(MainDashboardViewImpl form) {
        super(form, null, i18n.tr("Getting Started using myCommunity Resident Portal"), ThemeColor.contrast1, 1);
        asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());

        SafeHtmlBuilder contentHtmlBuilder = new SafeHtmlBuilder();

        contentHtmlBuilder.appendHtmlConstant(i18n
                .tr("Now you are ready to start using myCommunity Resident Portal on a regular basis. It's really up to you what you do next."));
        contentHtmlBuilder.appendHtmlConstant("<p/><ul>");
        String communicationId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<li><span id=\"" + communicationId + "\"></span></li>");
        String billingId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<li><span id=\"" + billingId + "\"></span></li>");
        String maintananceId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<li><span id=\"" + maintananceId + "\"></span></li>");
        String insuranceId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<li><span id=\"" + insuranceId + "\"></span></li>");
        String profileId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<li><span id=\"" + profileId + "\"></span></li>");
        contentHtmlBuilder.appendHtmlConstant("</ul>");

        HTMLPanel contentPanel = new HTMLPanel(contentHtmlBuilder.toSafeHtml());

        contentPanel.addAndReplaceElement(new PointerLink(i18n.tr("Communicate to your Property Management Office."), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Message.MessageView());
            }
        }, ResidentPortalPointerId.communication), communicationId);

        contentPanel.addAndReplaceElement(new PointerLink(i18n.tr("Make a payment, setup Auto Pay or see your Billing history."), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial());
            }
        }, ResidentPortalPointerId.billing), billingId);

        contentPanel.addAndReplaceElement(new PointerLink(i18n.tr("Submit Maintanance Request."), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Maintenance());
            }
        }, ResidentPortalPointerId.maintanance), maintananceId);

        contentPanel.addAndReplaceElement(new PointerLink(i18n.tr("Purchase Insurance."), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices());
            }
        }, ResidentPortalPointerId.insurance), insuranceId);

        contentPanel.addAndReplaceElement(new PointerLink(i18n.tr("Update your Profile."), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Profile());
            }
        }, ResidentPortalPointerId.profile), profileId);

        contentPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        setContent(contentPanel);

        setNavigationBar(new NavigationBar());

    }

    class NavigationBar extends FlowPanel {
        public NavigationBar() {
            if (!VistaFeatures.instance().yardiIntegration()) {

                Anchor anchor = new Anchor(i18n.tr("Hide Getting Started"), new Command() {

                    @Override
                    public void execute() {
                        getGadgetView().setGettingStartedGadgetOptOut(true);
                    }
                });
                anchor.setTitle(i18n.tr("You can use setting menu to show Getting Started again."));
                add(anchor);

            }
        }
    }

}
