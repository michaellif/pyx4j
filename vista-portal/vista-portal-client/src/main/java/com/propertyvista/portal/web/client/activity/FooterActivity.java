/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 19, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.rpc.portal.services.resident.FooterContentService;
import com.propertyvista.portal.rpc.portal.web.dto.PortalFooterContentDTO;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.FooterView;

public class FooterActivity extends AbstractActivity {

    private final FooterView view;

    private final FooterContentService service;

    private static final FooterActivity instance = new FooterActivity();

    private FooterActivity() {
        view = PortalWebSite.getViewFactory().instantiate(FooterView.class);
        service = GWT.<FooterContentService> create(FooterContentService.class);
        service.getFooterContent(new DefaultAsyncCallback<PortalFooterContentDTO>() {
            @Override
            public void onSuccess(PortalFooterContentDTO footerContent) {
                view.setContent(footerContent);
            }
        });
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

    }

    public static Activity instance() {
        return instance;
    }

}
