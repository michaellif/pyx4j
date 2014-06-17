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

import static com.propertyvista.domain.security.VistaCrmBehavior.AccountSelf;
import static com.propertyvista.domain.security.VistaCrmBehavior.EmployeeAdvance;
import static com.propertyvista.domain.security.VistaCrmBehavior.EmployeeFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.PortfolioBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.PortfolioFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.dto.company.EmployeePrivilegesDTO;
import com.propertyvista.crm.rpc.services.organization.CrmUserService;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;
import com.propertyvista.crm.rpc.services.organization.PortfolioCrudService;
import com.propertyvista.crm.rpc.services.organization.SelectCrmRoleListService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class VistaCrmEmployeeAccessControlList extends UIAclBuilder {

    VistaCrmEmployeeAccessControlList() {

        // ------ Account Self management
        //-- back-end
        {
            grant(AccountSelf, new IServiceExecutePermission(CrmUserService.class));
            grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CrmUserService.class));
        }

        // ------ Employee View and Management
        grant(EmployeeAdvance, EmployeeDTO.class, READ);
        grant(EmployeeFull, EmployeeDTO.class, ALL);
        grant(EmployeeFull, EmployeePrivilegesDTO.class, ALL);

        //-- back-end
        {
            grant(EmployeeAdvance, new IServiceExecutePermission(EmployeeCrudService.class));
            grant(EmployeeAdvance, new EntityPermission(Employee.class, READ));

            grant(EmployeeFull, new EntityPermission(Employee.class, ALL));
            grant(EmployeeFull, new IServiceExecutePermission(SelectCrmRoleListService.class));
            grant(EmployeeFull, new IServiceExecutePermission(ManagedCrmUserService.class));

        }
        grant(EmployeeAdvance, AccountSelf);
        grant(EmployeeFull, EmployeeAdvance);

        // grant(VistaCrmBehavior.EmployeeDefault, new ActionPermission(UpdateFromYardi.class));

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
