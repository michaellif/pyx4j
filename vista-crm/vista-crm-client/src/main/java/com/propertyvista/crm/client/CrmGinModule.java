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
package com.propertyvista.crm.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.propertyvista.common.client.VistaUnrecoverableErrorHandler;
import com.propertyvista.crm.client.mvp.MvpModule;
import com.propertyvista.crm.client.ui.ViewModule;
import com.propertyvista.crm.rpc.SiteMap;

import com.pyx4j.site.client.AppSiteGinModule;

public class CrmGinModule extends AppSiteGinModule {

    @Override
    protected void configure() {
        super.configure();

        bind(VistaUnrecoverableErrorHandler.class).asEagerSingleton();

        install(new MvpModule());

        install(new ViewModule());
    }

}