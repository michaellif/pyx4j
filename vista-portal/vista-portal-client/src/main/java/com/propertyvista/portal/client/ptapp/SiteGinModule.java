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
package com.propertyvista.portal.client.ptapp;

import com.propertyvista.common.client.VistaUnrecoverableErrorHandler;
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

}