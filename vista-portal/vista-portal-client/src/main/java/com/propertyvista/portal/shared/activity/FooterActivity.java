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
package com.propertyvista.portal.shared.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.rpc.portal.services.resident.PortalContentService;
import com.propertyvista.portal.rpc.portal.web.dto.PortalContentDTO;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.FooterView;

public class FooterActivity extends AbstractActivity {

    private final FooterView view;

    private final PortalContentService service;

    private static final FooterActivity instance = new FooterActivity();

    private FooterActivity() {
        view = PortalSite.getViewFactory().instantiate(FooterView.class);
        service = GWT.<PortalContentService> create(PortalContentService.class);
        service.getPortalContent(new DefaultAsyncCallback<PortalContentDTO>() {
            @Override
            public void onSuccess(PortalContentDTO content) {
                view.setContent(content);
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
