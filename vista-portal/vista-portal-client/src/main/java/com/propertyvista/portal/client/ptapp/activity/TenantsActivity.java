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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.ui.TenantsView;
import com.propertyvista.portal.domain.pt.PotentialTenant;
import com.propertyvista.portal.domain.pt.Tenants;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.place.AppPlace;

public class TenantsActivity extends AbstractActivity implements TenantsView.Presenter {

    private static final Logger log = LoggerFactory.getLogger(TenantsActivity.class);

    private final TenantsView view;

    //TODO FOR TESTING
    private static Tenants potentialTenantInfo = EntityFactory.create(Tenants.class);

    @Inject
    public TenantsActivity(TenantsView view) {
        this.view = view;
        view.setPresenter(this);

        //TODO: for testing
        PotentialTenant t = EntityFactory.create(PotentialTenant.class);
        potentialTenantInfo.tenants().add(t);
    }

    public TenantsActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        //TODO get real PotentialTenantInfo
        log.info("LOADED {}", potentialTenantInfo);
        view.populate(potentialTenantInfo);
    }

    @Override
    public void save(Tenants value) {
        log.info("SAVED {}", value);
    }
}
