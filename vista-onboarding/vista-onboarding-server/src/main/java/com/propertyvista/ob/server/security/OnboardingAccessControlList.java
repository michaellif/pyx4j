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
package com.propertyvista.ob.server.security;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessService;
import com.pyx4j.log4gwt.rpc.LogServices;
import com.pyx4j.rpc.shared.IServiceAdapter;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.ServletContainerAclBuilder;

import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;
import com.propertyvista.ob.rpc.services.OnboardingPublicActivationService;
import com.propertyvista.ob.rpc.services.PmcActivationService;

public class OnboardingAccessControlList extends ServletContainerAclBuilder {

    public OnboardingAccessControlList() {
        grant(new ServiceExecutePermission(LogServices.Log.class));
        grant(new ServiceExecutePermission(IServiceAdapter.class));
        grant(new IServiceExecutePermission(OnboardingAuthenticationService.class));
        grant(new IServiceExecutePermission(OnboardingPublicActivationService.class));

        grant(VistaBasicBehavior.Onboarding, new IServiceExecutePermission(PmcActivationService.class));
        grant(VistaBasicBehavior.Onboarding, new IServiceExecutePermission(DeferredProcessService.class));

        freeze();
    }

}
