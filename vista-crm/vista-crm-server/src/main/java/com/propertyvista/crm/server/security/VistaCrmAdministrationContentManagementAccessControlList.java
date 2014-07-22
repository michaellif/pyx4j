/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.AdminContent;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.services.CityIntroPageCrudService;
import com.propertyvista.crm.rpc.services.HomePageGadgetCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteBrandingCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteContentCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteGeneralCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.crm.rpc.services.admin.ac.CrmContentManagementAccess;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.SiteImageResource;

class VistaCrmAdministrationContentManagementAccessControlList extends UIAclBuilder {

    VistaCrmAdministrationContentManagementAccessControlList() {

        grant(AdminContent, CrmContentManagementAccess.class);

        grant(AdminContent, new IServiceExecutePermission(SiteGeneralCrudService.class));
        grant(AdminContent, new IServiceExecutePermission(SiteBrandingCrudService.class));
        grant(AdminContent, new IServiceExecutePermission(SiteContentCrudService.class));
        grant(AdminContent, new IServiceExecutePermission(SiteImageResourceCrudService.class));
        grant(AdminContent, new IServiceExecutePermission(SiteImageResourceUploadService.class));
        grant(AdminContent, new IServiceExecutePermission(CityIntroPageCrudService.class));
        grant(AdminContent, new IServiceExecutePermission(HomePageGadgetCrudService.class));

        grant(AdminContent, new EntityPermission(SiteDescriptor.class, READ | UPDATE));
        grant(AdminContent, new EntityPermission(SiteImageResource.class, READ));
        grant(AdminContent, new EntityPermission(AvailableLocale.class, READ));

    }
}
