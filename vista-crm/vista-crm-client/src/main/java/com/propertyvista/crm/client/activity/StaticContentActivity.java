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
package com.propertyvista.crm.client.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.crm.client.resources.CrmResources;
import com.propertyvista.crm.client.ui.StaticContentView;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

public class StaticContentActivity extends AbstractActivity implements StaticContentView.Presenter {

    private static final Logger log = LoggerFactory.getLogger(StaticContentActivity.class);

    private final StaticContentView view;

    private String content;

    @Inject
    public StaticContentActivity(StaticContentView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public StaticContentActivity withPlace(AppPlace place) {
        AppPlaceInfo info = AppSite.getHistoryMapper().getPlaceInfo(place);
        String resource = info.getResource();
        if (info.getResource() != null) {
            ResourcePrototype prototype = CrmResources.INSTANCE.getResource(resource);
            if (prototype != null && prototype instanceof TextResource) {
                setContent(((TextResource) prototype).getText());
            } else {
                setContent("");
                log.warn("No text resource found for " + resource);
            }
        } else {
            setContent("");
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
