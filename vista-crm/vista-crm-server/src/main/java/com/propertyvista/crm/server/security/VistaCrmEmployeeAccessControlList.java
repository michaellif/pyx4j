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

import static com.propertyvista.domain.security.VistaCrmBehavior.EmployeeBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.EmployeeFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.PortfolioBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.PortfolioFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.dto.company.EmployeePrivilegesDTO;
import com.propertyvista.crm.rpc.dto.company.ac.CRMUserSecurityActions;
import com.propertyvista.crm.rpc.dto.company.ac.EmployeeDirectoryList;
import com.propertyvista.crm.rpc.security.EmployeeSelfInstanceAccess;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;
import com.propertyvista.crm.rpc.services.organization.PortfolioCrudService;
import com.propertyvista.crm.rpc.services.organization.SelectCrmRoleListService;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.crm.rpc.services.security.CrmPasswordChangeUserService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

class VistaCrmEmployeeAccessControlList extends UIAclBuilder {

    VistaCrmEmployeeAccessControlList() {

        // -- Crm Users, Self management ==  All Users 
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmPasswordChangeUserService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmUserService.class));
        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmAccountRecoveryOptionsUserService.class));
        grant(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired, new IServiceExecutePermission(CrmAccountRecoveryOptionsUserService.class));

        // ------ Account Self management
        // There are no UI Permissions, UI bound to  VistaCrmBehavior.AccountSelf
        //-- back-end
        {
            grant(VistaBasicBehavior.CRM, DataModelPermission.create(EmployeeDTO.class, new EmployeeSelfInstanceAccess(), READ));
            // There are special  service for this, CrmUserService
        }

        // ------ Employee View and Management
        grant(EmployeeFull, new ActionPermission(EmployeeDirectoryList.class));
        grant(EmployeeBasic, EmployeeDTO.class, READ);
        grant(EmployeeFull, EmployeeDTO.class, ALL);
        grant(EmployeeFull, EmployeePrivilegesDTO.class, ALL);

        grant(EmployeeFull, new ActionPermission(CRMUserSecurityActions.class));

        //-- back-end
        {
            grant(EmployeeBasic, new IServiceExecutePermission(EmployeeCrudService.class));
            grant(EmployeeBasic, new EntityPermission(Employee.class, READ));

            grant(EmployeeFull, new EntityPermission(Employee.class, ALL));
            grant(EmployeeFull, new EntityPermission(CrmRole.class, READ));
            grant(EmployeeFull, new IServiceExecutePermission(SelectCrmRoleListService.class));
            grant(EmployeeFull, new IServiceExecutePermission(ManagedCrmUserService.class));

        }
        grant(EmployeeFull, EmployeeBasic);

        // ------ Portfolio Management

        grant(PortfolioBasic, Portfolio.class, READ);
        grant(PortfolioFull, Portfolio.class, ALL);
        //-- back-end
        {
            grant(PortfolioBasic, new IServiceExecutePermission(PortfolioCrudService.class));
            grant(PortfolioBasic, new EntityPermission(Portfolio.class, READ));
            grant(PortfolioFull, new EntityPermission(Portfolio.class, ALL));

        }
        grant(PortfolioFull, PortfolioBasic);

    }
}
