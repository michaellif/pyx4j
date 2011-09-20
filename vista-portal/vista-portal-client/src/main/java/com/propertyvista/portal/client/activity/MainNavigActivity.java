/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.ui.MainNavigView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents;

public class MainNavigActivity extends AbstractActivity implements MainNavigView.MainNavigPresenter {

    private final MainNavigView view;

    private final AppPlace place;

    private static I18n i18n = I18nFactory.getI18n(MainNavigActivity.class);

    public MainNavigActivity(Place place) {
        this.view = (MainNavigView) PortalViewFactory.instance(MainNavigView.class);
        view.setPresenter(this);
        this.place = (AppPlace) place;
        withPlace(place);
    }

    public MainNavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        List<NavigItem> items = new ArrayList<NavigItem>();
        items.add(new NavigItem(new Residents.PersonalInfo(), i18n.tr("Personal Info")));
        items.add(new NavigItem(new Residents.CurrentBill(), i18n.tr("Current Bill")));
        items.add(new NavigItem(new Residents.PaymentMethods(), i18n.tr("Payment Methods")));
        items.add(new NavigItem(new Residents.BillingHistory(), i18n.tr("Billing History")));
        items.add(new NavigItem(new Residents.Maintenance(), i18n.tr("Maintenance")));
        view.setMainNavig(items);

    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

}
