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
import static com.propertyvista.domain.security.VistaCrmBehavior.AdminFinancial;
import static com.propertyvista.domain.security.VistaCrmBehavior.AdminGeneral;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.admin.PmcCompanyInfoDTO;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.crm.rpc.services.admin.CrmRoleCrudService;
import com.propertyvista.crm.rpc.services.admin.PmcCompanyInfoCrudService;
import com.propertyvista.crm.rpc.services.admin.PmcPaymentMethodsCrudService;
import com.propertyvista.crm.rpc.services.admin.ac.CrmAdministrationAccess;
import com.propertyvista.crm.rpc.services.admin.ac.GlobalTenantSecurity;
import com.propertyvista.crm.rpc.services.customer.EmailToTenantsService;
import com.propertyvista.crm.rpc.services.customer.ExportTenantsService;
import com.propertyvista.crm.rpc.services.security.CrmAuditRecordsListerService;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.dto.AuditRecordDTO;
import com.propertyvista.operations.domain.security.AuditRecord;

class VistaCrmAdministrationAccessControlList extends UIAclBuilder {

    VistaCrmAdministrationAccessControlList() {
        grant(AdminGeneral, CrmAdministrationAccess.class);
        grant(AdminFinancial, CrmAdministrationAccess.class);
        grant(AdminContent, CrmAdministrationAccess.class);

        // Profile

        grant(AdminGeneral, new IServiceExecutePermission(PmcCompanyInfoCrudService.class));
        grant(AdminGeneral, PmcCompanyInfoDTO.class, ALL);

        grant(AdminFinancial, new IServiceExecutePermission(PmcPaymentMethodsCrudService.class));
        grant(AdminFinancial, PmcPaymentMethodsDTO.class, ALL);

        // Security

        grant(AdminGeneral, new IServiceExecutePermission(CrmAuditRecordsListerService.class));
        grant(AdminGeneral, new EntityPermission(AuditRecord.class, READ));
        grant(AdminGeneral, AuditRecordDTO.class, READ);

        grant(AdminGeneral, new IServiceExecutePermission(CrmRoleCrudService.class));
        grant(AdminGeneral, CrmRole.class, ALL);

        grant(AdminGeneral, GlobalTenantSecurity.class);
        grant(AdminGeneral, new IServiceExecutePermission(ExportTenantsService.class));
        grant(AdminGeneral, new IServiceExecutePermission(EmailToTenantsService.class));
    }

}
