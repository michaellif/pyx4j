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
import com.propertyvista.portal.tester.ui.LeftPortletsView;

import com.pyx4j.site.rpc.AppPlace;

public class LeftPortletsActivity extends AbstractActivity implements LeftPortletsView.Presenter {

    private final LeftPortletsView view;

    private String content;

    @Inject
    public LeftPortletsActivity(LeftPortletsView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public LeftPortletsActivity withPlace(AppPlace place) {
        setContent("<h3>Portlet</h3><div>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</div>");
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
