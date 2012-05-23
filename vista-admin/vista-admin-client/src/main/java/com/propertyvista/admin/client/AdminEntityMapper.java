/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client;

import static com.pyx4j.site.client.AppPlaceEntityMapper.register;

import com.google.gwt.resources.client.ImageResource;

import com.propertyvista.admin.client.resources.AdminImages;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimFile;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.domain.security.AdminUser;
import com.propertyvista.domain.security.OnboardingUser;

public class AdminEntityMapper {

    private static ImageResource DEFAULT_IMAGE = AdminImages.INSTANCE.blank();

    public static void init() {
        register(AdminUser.class, AdminSiteMap.Administration.AdminUsers.class, DEFAULT_IMAGE);
        register(OnboardingUser.class, AdminSiteMap.Management.OnboardingUsers.class, DEFAULT_IMAGE);
        register(PmcDTO.class, AdminSiteMap.Management.PMC.class, DEFAULT_IMAGE);
        register(Trigger.class, AdminSiteMap.Management.Trigger.class, DEFAULT_IMAGE);
        register(Run.class, AdminSiteMap.Management.Run.class, DEFAULT_IMAGE);
        register(PadSimFile.class, AdminSiteMap.Administration.PadSimulation.PadSimFile.class, DEFAULT_IMAGE);
        register(PadSimBatch.class, AdminSiteMap.Administration.PadSimulation.PadSimBatch.class, DEFAULT_IMAGE);
    }
}
