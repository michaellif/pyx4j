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
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.marketing.PortalResidentMarketingTarget;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.extra.QuickTipView;
import com.propertyvista.portal.resident.ui.extra.QuickTipView.QuickTipPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.MoveIn;
import com.propertyvista.portal.rpc.portal.resident.dto.QuickTipTO;
import com.propertyvista.portal.rpc.portal.resident.services.QuickTipService;

public class QuickTipActivity extends AbstractActivity implements QuickTipPresenter {

    private static final I18n i18n = I18n.get(QuickTipActivity.class);

    private final QuickTipView view;

    private final AppPlace place;

    private static Vector<QuickTipTO> quickTips = null;

    private static int currentQuickTipIndex = -1;

    public QuickTipActivity(Place place) {
        this.place = (AppPlace) place;
        view = ResidentPortalSite.getViewFactory().getView(QuickTipView.class);
    }

    private void obtainTips(AsyncCallback<Vector<QuickTipTO>> callback) {
        if (quickTips != null) {
            callback.onSuccess(quickTips);
        } else {
            GWT.<QuickTipService> create(QuickTipService.class).getQuickTips(callback);
        }
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {

        obtainTips(new AsyncCallback<Vector<QuickTipTO>>() {

            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(Vector<QuickTipTO> result) {
                if (result == null) {
                    return;
                }

                if (quickTips == null) {
                    quickTips = new Vector<QuickTipTO>();
                    quickTips.addAll(result);
                }

                List<QuickTipTO> tips = new ArrayList<QuickTipTO>();

                if (!place.getPlaceId().startsWith(new MoveIn().getPlaceId())) {

                    if (!SecurityController.check(PortalResidentBehavior.AutopayAgreementPresent)) {
                        tips.addAll(getTips(PortalResidentMarketingTarget.AutopayAgreementNotSetup));
                    }

                    if (!SecurityController.check(PortalResidentBehavior.InsurancePresent)) {
                        tips.addAll(getTips(PortalResidentMarketingTarget.InsuranceMissing));
                    }

                    tips.addAll(getTips(PortalResidentMarketingTarget.Other));
                }

                if (tips.size() > 0) {
                    panel.setWidget(view);
                    AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
                    int random = getRandomTip(currentQuickTipIndex, tips.size());
                    setTip(tips.get(random));
                }
            }
        });

    }

    private int getRandomTip(int current, int size) {

        int random = new Random().nextInt(size);

        if (size > 1) {
            while (random == currentQuickTipIndex) {
                random = new Random().nextInt(size);
            }
        }

        currentQuickTipIndex = random;

        return random;
    }

    private List<QuickTipTO> getTips(PortalResidentMarketingTarget target) {
        if (quickTips == null) {
            return null;
        }

        List<QuickTipTO> targetedTips = new ArrayList<QuickTipTO>();
        for (QuickTipTO tip : quickTips) {
            if (tip.target().getValue() == target) {
                targetedTips.add(tip);
            }
        }

        return targetedTips;
    }

    private void setTip(QuickTipTO tip) {
        SafeHtmlBuilder contentHtmlBuilder = new SafeHtmlBuilder();
        contentHtmlBuilder.appendHtmlConstant(tip.content().getValue());
        HTMLPanel contentPanel = new HTMLPanel(contentHtmlBuilder.toSafeHtml());
        view.setQuickTip(contentPanel, null);
    }

}
