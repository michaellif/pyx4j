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
import com.propertyvista.portal.client.ptapp.ui.InfoView;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.rpc.pt.PotencialTenantServices;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.client.place.AppPlace;

public class InfoActivity extends AbstractActivity implements InfoView.Presenter {

    private static final Logger log = LoggerFactory.getLogger(InfoActivity.class);

    private final InfoView view;

    private static PotentialTenantInfo potentialTenantInfo;

    @Inject
    public InfoActivity(InfoView view) {
        this.view = view;
        view.setPresenter(this);

    }

    public InfoActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        RPCManager.execute(PotencialTenantServices.RetrieveByPK.class, EntityCriteriaByPK.create(PotentialTenantInfo.class, potentialTenantInfo),
                new DefaultAsyncCallback<IEntity>() {

                    @Override
                    public void onSuccess(IEntity result) {
                        potentialTenantInfo = (PotentialTenantInfo) result;
                        log.info("LOADED {}", potentialTenantInfo);
                        view.populate(potentialTenantInfo);
                    }
                });

    }

    @Override
    public void save(PotentialTenantInfo entity) {
        RPCManager.execute(PotencialTenantServices.Save.class, entity, new DefaultAsyncCallback<IEntity>() {

            @Override
            public void onSuccess(IEntity result) {
                log.info("SAVED {}", result);
                potentialTenantInfo = (PotentialTenantInfo) result;
            }
        });
    }

}
