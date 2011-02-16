/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.client.ptapp;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.rpc.pt.PotencialTenantServices;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;

public class PtAppWizardManager implements SecurityControllerHandler {

    private Application application;

    private final EventBus eventBus;

    private final PlaceController placeController;

    @Inject
    public PtAppWizardManager(EventBus eventBus, PlaceController placeController) {
        this.eventBus = eventBus;
        this.placeController = placeController;
        eventBus.addHandler(SecurityControllerEvent.getType(), this);
    }

    public void saveApplicationProgress() {

        RPCManager.execute(PotencialTenantServices.Save.class, application, new DefaultAsyncCallback<IEntity>() {
            @Override
            public void onSuccess(IEntity result) {
                application = (Application) result;
            }
        });
    }

    protected void goToNext() {
        Place current = placeController.getWhere();
        placeController.goTo(new SiteMap.Apartment());
    }

    @Override
    public void onSecurityContextChange(SecurityControllerEvent event) {
        if (ClientSecurityController.checkBehavior(VistaBehavior.POTENCIAL_TENANT)) {
            RPCManager.execute(PotencialTenantServices.GetCurrentApplication.class, null, new DefaultAsyncCallback<Application>() {

                @Override
                public void onSuccess(Application result) {
                    application = result;
                    goToNext();
                }
            });
        } else {
            application = null;
            placeController.goTo(new SiteMap.CreateAccount());
        }
    }
}
