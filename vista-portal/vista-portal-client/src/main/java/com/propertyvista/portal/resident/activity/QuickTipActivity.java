/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.ContentLink;
import com.propertyvista.portal.resident.ui.extra.QuickTipView;
import com.propertyvista.portal.resident.ui.extra.QuickTipView.QuickTipPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.Maintenance;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.MoveIn;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.ResidentServices;
import com.propertyvista.portal.shared.resources.PortalImages;

public class QuickTipActivity extends AbstractActivity implements QuickTipPresenter {

    private static final I18n i18n = I18n.get(QuickTipActivity.class);

    private final QuickTipView view;

    private final AppPlace place;

    enum Tip {
        pap, insurance, maintenance
    }

    public QuickTipActivity(Place place) {
        this.place = (AppPlace) place;
        view = ResidentPortalSite.getViewFactory().getView(QuickTipView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        List<Tip> tips = new ArrayList<>();

        if (!place.getPlaceId().startsWith(new MoveIn().getPlaceId())) {

            if (!SecurityController.check(PortalResidentBehavior.AutopayAgreementPresent)) {
                tips.add(Tip.pap);
            }

            if (!SecurityController.check(PortalResidentBehavior.InsurancePresent) && !place.getPlaceId().startsWith(new ResidentServices().getPlaceId())) {
                tips.add(Tip.insurance);
            }

            if (!place.getPlaceId().startsWith(new Maintenance().getPlaceId())) {
                tips.add(Tip.maintenance);
            }

        }

        if (tips.size() > 0) {
            panel.setWidget(view);
            AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));

            int random = new Random().nextInt(tips.size());
            setTip(tips.get(random));
        }

    }

    private void setTip(Tip tip) {
        switch (tip) {
        case pap:
            setTip(i18n.tr("Pre-authorized payments"),
                    i18n.tr("Paying your rent by pre-authorized payments means eliminating the chore of writing cheques and ensuring your payment reaches Property Management Office by the due date. You'll never have to worry about remembering to make a payment or a possible late fee."),
                    i18n.tr("Visit Billing & Payment page."), ThemeColor.contrast4, new Command() {

                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial());
                        }
                    });
            break;
        case insurance:
            setTip(i18n.tr("Don't have Tenant Insurance yet?"),
                    i18n.tr("We have teamed up with Highcourt Partners Limited, a licensed broker, to assist you in obtaining your Tenant Insurance."),
                    i18n.tr("Visit Resident Services page."), ThemeColor.contrast3, new Command() {

                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.ResidentServices());
                        }
                    });
            break;
        case maintenance:
            setTip(i18n.tr("Request repairs and maintenance as needed"),
                    i18n.tr("Submit and track the status of a maintenance request. Convenient, simple, easy."), i18n.tr("Visit Maintenance page."),
                    ThemeColor.contrast5, new Command() {

                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Maintenance());
                        }
                    });
            break;
        default:
            break;

        }
    }

    private void setTip(String caption, String text, String visitText, ThemeColor color, Command command) {
        SafeHtmlBuilder contentHtmlBuilder = new SafeHtmlBuilder();

        String imageId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<div style='min-height:45px; position:relative'><div style='position:absolute'><span id=\"" + imageId
                + "\"></span></div>");
        contentHtmlBuilder.appendHtmlConstant("<div style='display:inline-block; margin-left:45px'>");
        contentHtmlBuilder.appendHtmlConstant(caption);
        contentHtmlBuilder.appendHtmlConstant("</div></div><p/><div style='font-size:0.8em'>");
        contentHtmlBuilder.appendHtmlConstant(text);
        contentHtmlBuilder.appendHtmlConstant("</div><p/>");
        String visitId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<span id=\"" + visitId + "\"></span>");

        HTMLPanel contentPanel = new HTMLPanel(contentHtmlBuilder.toSafeHtml());
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        contentPanel.addAndReplaceElement(new Image(PortalImages.INSTANCE.tip()), imageId);

        contentPanel.addAndReplaceElement(new ContentLink(i18n.tr("<i style='font-size:0.8em'>" + visitText + "</i>"), command), visitId);

        view.setQuickTip(contentPanel, color);

    }
}
