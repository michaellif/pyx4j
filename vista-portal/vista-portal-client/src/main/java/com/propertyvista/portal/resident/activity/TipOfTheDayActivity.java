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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.ResidentPortalPointerId;
import com.propertyvista.portal.resident.ui.extra.TipOfTheDayView;
import com.propertyvista.portal.resident.ui.extra.TipOfTheDayView.TipOfTheDayPresenter;

public class TipOfTheDayActivity extends AbstractActivity implements TipOfTheDayPresenter {

    private final TipOfTheDayView view;

    public TipOfTheDayActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().getView(TipOfTheDayView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));

        view.setTipOfTheDay(
                "Paying your rent by pre-authorized payments means eliminating the chore of writing cheques and ensuring your payment reaches Property Management Office by the due date. You'll never have to worry about remembering to make a payment or a possible late fee.",
                ThemeColor.contrast4, ResidentPortalPointerId.billing, new Command() {

                    @Override
                    public void execute() {
                        // TODO Auto-generated method stub

                    }
                });

    }

}
