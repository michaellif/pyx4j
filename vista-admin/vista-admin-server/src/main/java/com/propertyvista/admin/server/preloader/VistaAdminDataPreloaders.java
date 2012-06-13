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
package com.propertyvista.admin.server.preloader;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;

import com.propertyvista.preloader.OnboardingPmcPreloader;
import com.propertyvista.preloader.OnboardingUserPreloader;

public class VistaAdminDataPreloaders extends DataPreloaderCollection {

    public VistaAdminDataPreloaders() {
        add(new AdminUsersPreloader());
        add(new OnboardingUserPreloader());
        add(new OnboardingPmcPreloader());
        add(new TriggerPreloader());
        if (ApplicationMode.isDevelopment()) {
            add(new DevelopmentSecurityPreloader());
        }
    }
}
