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
import static com.propertyvista.domain.security.VistaCrmBehavior.TenantFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.security.FormerTenantInstanceAccess;
import com.propertyvista.crm.rpc.security.FormerTenantScreeningInstanceAccess;
import com.propertyvista.crm.rpc.security.TenantInstanceAccess;
import com.propertyvista.crm.rpc.security.TenantScreeningInstanceAccess;
import com.propertyvista.crm.rpc.services.customer.TenantPasswordChangeService;
import com.propertyvista.crm.rpc.services.customer.ac.FormerTenantListAction;
import com.propertyvista.crm.rpc.services.customer.ac.TenantChangePassword;
import com.propertyvista.crm.rpc.services.customer.ac.TenantListAction;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;

class VistaCrmTenantAccessControlList extends UIAclBuilder {

    VistaCrmTenantAccessControlList() {
        grant(TenantBasic, TenantDTO.class, new TenantInstanceAccess(), READ);
        grant(TenantAdvanced, TenantDTO.class, new TenantInstanceAccess(), READ);
        grant(TenantFull, TenantDTO.class, new TenantInstanceAccess(), READ | UPDATE);

        grant(TenantAdvanced, LeaseParticipantScreeningTO.class, new TenantInstanceAccess(), READ);
        grant(TenantAdvanced, LeaseParticipantScreeningTO.class, new TenantScreeningInstanceAccess(), READ);

        grant(TenantFull, LeaseParticipantScreeningTO.class, new TenantInstanceAccess(), ALL);
        grant(TenantFull, LeaseParticipantScreeningTO.class, new TenantScreeningInstanceAccess(), ALL);

        grant(TenantBasic, TenantPortalAccessInformationDTO.class, READ);
        grant(TenantAdvanced, TenantPortalAccessInformationDTO.class, READ);
        grant(TenantFull, TenantPortalAccessInformationDTO.class, READ);

        // Actions:
        grant(TenantBasic, TenantListAction.class);
        grant(TenantAdvanced, TenantListAction.class);
        grant(TenantFull, TenantListAction.class);

        grant(TenantBasic, TenantChangePassword.class, new TenantInstanceAccess());
        grant(TenantAdvanced, TenantChangePassword.class, new TenantInstanceAccess());
        grant(TenantFull, TenantChangePassword.class, new TenantInstanceAccess());
        grant(TenantBasic, TenantAdvanced, TenantFull, new IServiceExecutePermission(TenantPasswordChangeService.class));

// --------------------------------------------------------------------------------------------------------------------

        // Former Tenants:
        grant(TenantAdvanced, TenantDTO.class, new FormerTenantInstanceAccess(), READ);
        grant(TenantFull, TenantDTO.class, new FormerTenantInstanceAccess(), READ | UPDATE);

        grant(TenantAdvanced, LeaseParticipantScreeningTO.class, new FormerTenantInstanceAccess(), READ);
        grant(TenantAdvanced, LeaseParticipantScreeningTO.class, new FormerTenantScreeningInstanceAccess(), READ);

        grant(TenantFull, LeaseParticipantScreeningTO.class, new FormerTenantInstanceAccess(), READ | UPDATE);
        grant(TenantFull, LeaseParticipantScreeningTO.class, new FormerTenantScreeningInstanceAccess(), READ | UPDATE);

        // Actions:
        grant(TenantAdvanced, FormerTenantListAction.class);
        grant(TenantFull, FormerTenantListAction.class);
    }
}
