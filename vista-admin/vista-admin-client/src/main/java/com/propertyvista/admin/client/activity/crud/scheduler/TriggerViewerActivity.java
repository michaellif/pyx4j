/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.scheduler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.admin.client.ui.crud.scheduler.trigger.TriggerViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.rpc.services.scheduler.TriggerCrudService;

public class TriggerViewerActivity extends ViewerActivityBase<Trigger> implements TriggerViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public TriggerViewerActivity(Place place) {
        super(place, ManagementVeiwFactory.instance(TriggerViewerView.class), (AbstractCrudService<Trigger>) GWT.create(TriggerCrudService.class));
    }

    @Override
    public void runImmediately() {
        GWT.<TriggerCrudService> create(TriggerCrudService.class).runImmediately(new DefaultAsyncCallback<Run>() {
            @Override
            public void onSuccess(Run result) {
                //TODO
                System.out.println("TODO - navigate to run view");
            }
        }, EntityFactory.createIdentityStub(Trigger.class, entityId));
    }
}
