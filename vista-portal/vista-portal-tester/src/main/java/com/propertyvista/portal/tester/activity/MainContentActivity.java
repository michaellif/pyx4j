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
package com.propertyvista.portal.tester.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.tester.SiteMap.AboutUs;
import com.propertyvista.portal.tester.SiteMap.ContactUs;
import com.propertyvista.portal.tester.SiteMap.Home;
import com.propertyvista.portal.tester.SiteMap.Landing;
import com.propertyvista.portal.tester.resources.SiteResources;
import com.propertyvista.portal.tester.ui.MainContentView;

import com.pyx4j.site.rpc.AppPlace;

public class MainContentActivity extends AbstractActivity implements MainContentView.Presenter {

    private final MainContentView view;

    private String content;

    @Inject
    public MainContentActivity(MainContentView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public MainContentActivity withPlace(AppPlace place) {
        if (Landing.class.equals(place.getClass())) {
            setContent(SiteResources.INSTANCE.landing().getText());
        } else if (Home.class.equals(place.getClass())) {
            setContent(SiteResources.INSTANCE.home().getText());
        } else if (AboutUs.class.equals(place.getClass())) {
            setContent(SiteResources.INSTANCE.aboutUs().getText());
        } else if (ContactUs.class.equals(place.getClass())) {
            setContent(SiteResources.INSTANCE.contactUs().getText());
        } else {
            setContent(place.toString() + (place).getArgs());
        }
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        view.setContent(content);
    }

}
