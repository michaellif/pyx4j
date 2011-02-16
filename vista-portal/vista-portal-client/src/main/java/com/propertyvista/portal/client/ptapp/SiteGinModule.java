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
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.mvp.MvpModule;
import com.propertyvista.portal.client.ptapp.ui.ViewModule;

public class SiteGinModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(VistaUnrecoverableErrorHandler.class).asEagerSingleton();

        install(new MvpModule());
        install(new ViewModule());
    }
}