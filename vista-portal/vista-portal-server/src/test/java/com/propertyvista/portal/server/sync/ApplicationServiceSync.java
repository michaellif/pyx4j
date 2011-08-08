/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.sync;

import org.junit.Assert;

import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;

import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;

public class ApplicationServiceSync {

    private Application currentApplication;

    public Application getCurrentApplication() {
        currentApplication = null;

        ApplicationService applicationService = TestServiceFactory.create(ApplicationService.class);
        applicationService.getApplication(new UnitTestsAsyncCallback<Application>() {
            @Override
            public void onSuccess(Application result) {
                Assert.assertNotNull("Application", result);
                currentApplication = result;
            }
        });

        Assert.assertNotNull(currentApplication);
        return currentApplication;
    }
}
