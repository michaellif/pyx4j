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

import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.rpc.pt.PotencialTenantServices;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.shared.Behavior;

public class PtAppWizardManager {

    private Application application;

    private final EventBus eventBus;

    private final PlaceController placeController;

    @Inject
    public PtAppWizardManager(EventBus eventBus, PlaceController placeController) {
        this.eventBus = eventBus;
        this.placeController = placeController;
        ClientSecurityController.instance().addValueChangeHandler(new ValueChangeHandler<Set<Behavior>>() {
            @Override
            public void onValueChange(ValueChangeEvent<Set<Behavior>> event) {
                onSecurityControllerEvent();
            }
        });

    }

    public void saveApplicationProgress() {

        RPCManager.execute(PotencialTenantServices.Save.class, application, new AsyncCallback<IEntity>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);

            }

            @Override
            public void onSuccess(IEntity result) {
                application = (Application) result;
            }
        });
    }

    protected void onSecurityControllerEvent() {
        if (ClientSecurityController.checkBehavior(VistaBehavior.POTENCIAL_TENANT)) {
            RPCManager.execute(PotencialTenantServices.GetCurrentApplication.class, null, new AsyncCallback<Application>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }

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

    protected void goToNext() {
        Place current = placeController.getWhere();
        placeController.goTo(new SiteMap.Apartment());
    }
}
