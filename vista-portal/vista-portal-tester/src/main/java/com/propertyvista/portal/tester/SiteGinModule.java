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
package com.propertyvista.portal.tester;

import com.google.gwt.inject.client.AbstractGinModule;
import com.propertyvista.portal.tester.activity.ActivityModule;
import com.propertyvista.portal.tester.mvp.MvpModule;
import com.propertyvista.portal.tester.ui.ViewModule;

public class SiteGinModule extends AbstractGinModule {

    @Override
    protected void configure() {

        install(new MvpModule());
        install(new ViewModule());
        install(new ActivityModule());
    }
}