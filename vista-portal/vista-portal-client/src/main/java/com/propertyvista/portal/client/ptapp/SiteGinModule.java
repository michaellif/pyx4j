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
package com.propertyvista.portal.client.ptapp;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.mvp.MvpModule;
import com.propertyvista.portal.client.ptapp.ui.ViewModule;

import com.pyx4j.site.client.AppSiteGinModule;

public class SiteGinModule extends AppSiteGinModule {

    @Override
    protected void configure() {
        super.configure();

        bind(VistaUnrecoverableErrorHandler.class).asEagerSingleton();

        install(new MvpModule());

        install(new ViewModule());
    }

    @Override
    @Provides
    @Singleton
    public PlaceHistoryHandler getHistoryHandler(PlaceController placeController, EventBus eventBus) {
        PlaceHistoryHandler historyHandler = super.getHistoryHandler(placeController, eventBus);
        historyHandler.register(placeController, eventBus, new SiteMap.CreateAccount());
        return historyHandler;
    }

    @Provides
    @Singleton
    public PtAppWizardManager getWizardManager(PlaceController placeController, EventBus eventBus) {
        return new PtAppWizardManager(placeController, eventBus);
    }

}