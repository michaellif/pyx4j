/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.preloader;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.domain.security.VistaOnboardingBehavior;

public class OnboardingUserPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        ServerSideFactory.create(UserManagementFacade.class).createOnboardingUser("PV Admin", "", "pvtestadm1n@gmail.com", "12345",
                VistaOnboardingBehavior.OnboardingAdministrator, "_a");
        ServerSideFactory.create(UserManagementFacade.class).createOnboardingUser("Leonard", "Drimmer", "leonard@propertyvista.com",
                "leonard@propertyvista.com", VistaOnboardingBehavior.OnboardingAdministrator, "_l");
        ServerSideFactory.create(UserManagementFacade.class).createOnboardingUser("Equifax Admin", "", "pvtestequifax@gmail.com", "12345",
                VistaOnboardingBehavior.Equifax, "_e");
        ServerSideFactory.create(UserManagementFacade.class).createOnboardingUser("Caledon Admin", "", "pvtestcaledon@gmail.com", "12345",
                VistaOnboardingBehavior.Caledon, "_c");

        return "Created " + 3 + " Onboarding Users";
    }

    @Override
    public String delete() {
        return null;
    }

}
