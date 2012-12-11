/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.server.services;

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.ob.rpc.services.OnboardingPublicActivationService;
import com.propertyvista.server.jobs.TaskRunner;

public class OnboardingPublicActivationServiceImpl implements OnboardingPublicActivationService {

    @Override
    public void checkDNSAvailability(AsyncCallback<Boolean> callback, final String dnsName) {
        callback.onSuccess(TaskRunner.runInAdminNamespace(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return ServerSideFactory.create(PmcFacade.class).checkDNSAvailability(dnsName);
            }

        }));
    }
}
