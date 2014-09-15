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
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.PointerLink;
import com.propertyvista.portal.resident.ui.ResidentPortalPointerId;
import com.propertyvista.portal.resident.ui.extra.QuickTipView;
import com.propertyvista.portal.resident.ui.extra.QuickTipView.QuickTipPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;

public class QuickTipActivity extends AbstractActivity implements QuickTipPresenter {

    private static final I18n i18n = I18n.get(QuickTipActivity.class);

    private final QuickTipView view;

    public QuickTipActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().getView(QuickTipView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
        setPapTip();
    }

    private void setPapTip() {
        SafeHtmlBuilder contentHtmlBuilder = new SafeHtmlBuilder();

        String imageId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<span id=\"" + imageId + "\"></span>");
        contentHtmlBuilder
                .appendHtmlConstant(i18n
                        .tr("<div style='display:inline-block;'>Pre-authorized payment</div><p/><div style='font-size:0.8em'>Paying your rent by pre-authorized payments means eliminating the chore of writing cheques and ensuring your payment reaches Property Management Office by the due date. You'll never have to worry about remembering to make a payment or a possible late fee.</div>"));
        contentHtmlBuilder.appendHtmlConstant("<p/>");
        String visitId = HTMLPanel.createUniqueId();
        contentHtmlBuilder.appendHtmlConstant("<span id=\"" + visitId + "\"></span>");

        HTMLPanel contentPanel = new HTMLPanel(contentHtmlBuilder.toSafeHtml());
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        contentPanel.addAndReplaceElement(new Image(PortalImages.INSTANCE.tip()), imageId);

        contentPanel.addAndReplaceElement(new PointerLink(i18n.tr("<i style='font-size:0.8em'>Visit Billing & Payment page.</i>"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Financial());
            }
        }, ResidentPortalPointerId.billing), visitId);

        view.setQuickTip(contentPanel, ThemeColor.contrast4);

    }
}
