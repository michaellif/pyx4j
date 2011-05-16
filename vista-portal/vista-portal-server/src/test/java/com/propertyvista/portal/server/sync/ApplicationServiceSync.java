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

import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.ptapp.CurrentApplication;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;

import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;

public class ApplicationServiceSync {
    private CurrentApplication currentApplication;

    public CurrentApplication getCurrentApplication(UnitSelectionCriteria unitSelectionCriteria) {
        currentApplication = null;

        ApplicationService applicationService = TestServiceFactory.create(ApplicationService.class);
        applicationService.getCurrentApplication(new UnitTestsAsyncCallback<CurrentApplication>() {
            @Override
            public void onSuccess(CurrentApplication result) {
                Assert.assertNotNull("Application", result.application);
                Assert.assertFalse("Application", result.application.isNull());
                currentApplication = result;
            }
        }, unitSelectionCriteria);

        Assert.assertNotNull(currentApplication);
        Assert.assertNotNull(currentApplication.application);
        return currentApplication;
    }
}
