/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 17, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.TenantAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.TenantBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.TenantFinancial;
import static com.propertyvista.domain.security.VistaCrmBehavior.TenantFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.security.TenantInstanceAccess;
import com.propertyvista.crm.rpc.services.customer.ac.TenantChangePassword;
import com.propertyvista.crm.rpc.services.customer.ac.TenantListAction;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;

class VistaCrmTenantAccessControlList extends UIAclBuilder {

    VistaCrmTenantAccessControlList() {
        { // 
            grant(TenantBasic, TenantDTO.class, new TenantInstanceAccess(), READ);
            grant(TenantAdvanced, TenantDTO.class, new TenantInstanceAccess(), READ);
            grant(TenantFull, TenantDTO.class, new TenantInstanceAccess(), READ | UPDATE);

            grant(TenantFinancial, TenantDTO.class, new TenantInstanceAccess(), READ);
        }

        grant(TenantBasic, TenantPortalAccessInformationDTO.class, READ);
        grant(TenantAdvanced, TenantPortalAccessInformationDTO.class, READ);
        grant(TenantFull, TenantPortalAccessInformationDTO.class, READ);

        // Actions:
        grant(TenantBasic, new ActionPermission(TenantListAction.class));
        grant(TenantAdvanced, new ActionPermission(TenantListAction.class));
        grant(TenantFull, new ActionPermission(TenantListAction.class));
        grant(TenantFinancial, new ActionPermission(TenantListAction.class));

        grant(TenantBasic, TenantChangePassword.class, new TenantInstanceAccess());
        grant(TenantAdvanced, TenantChangePassword.class, new TenantInstanceAccess());
        grant(TenantFull, TenantChangePassword.class, new TenantInstanceAccess());
        grant(TenantFinancial, TenantChangePassword.class, new TenantInstanceAccess());
    }
}
