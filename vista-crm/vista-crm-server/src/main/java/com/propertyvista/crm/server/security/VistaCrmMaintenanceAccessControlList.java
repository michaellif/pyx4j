/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.MaintenanceAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.MaintenanceBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.MaintenanceFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.CREATE;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.crm.rpc.services.maintenance.MaintenanceRequestPictureUploadService;
import com.propertyvista.crm.rpc.services.maintenance.ac.Cancel;
import com.propertyvista.crm.rpc.services.maintenance.ac.Resolve;
import com.propertyvista.crm.rpc.services.maintenance.ac.Schedule;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class VistaCrmMaintenanceAccessControlList extends UIAclBuilder {

    VistaCrmMaintenanceAccessControlList() {

        grant(MaintenanceBasic, MaintenanceRequestDTO.class, READ);
        grant(MaintenanceBasic, new ActionPermission(Schedule.class));
        grant(MaintenanceBasic, new IServiceExecutePermission(MaintenanceCrudService.class));

        grant(MaintenanceAdvanced, MaintenanceRequestDTO.class, READ | CREATE);
        grant(MaintenanceAdvanced, new ActionPermission(Schedule.class));
        grant(MaintenanceAdvanced, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(MaintenanceAdvanced, new IServiceExecutePermission(MaintenanceRequestPictureUploadService.class));

        grant(MaintenanceFull, MaintenanceRequestDTO.class, ALL);
        grant(MaintenanceFull, new ActionPermission(Schedule.class));
        grant(MaintenanceFull, new ActionPermission(Resolve.class));
        grant(MaintenanceFull, new ActionPermission(Cancel.class));
        grant(MaintenanceFull, new IServiceExecutePermission(MaintenanceCrudService.class));
        grant(MaintenanceFull, new IServiceExecutePermission(MaintenanceRequestPictureUploadService.class));

    }
}
