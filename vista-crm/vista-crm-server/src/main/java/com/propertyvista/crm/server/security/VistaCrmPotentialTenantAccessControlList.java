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

import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantScreening;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.security.PotentialTenantInstanceAccess;
import com.propertyvista.crm.rpc.security.PotentialTenantScreeningInstanceAccess;
import com.propertyvista.crm.rpc.services.customer.ac.PotentialTenantListAction;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.dto.TenantDTO;

public class VistaCrmPotentialTenantAccessControlList extends UIAclBuilder {

    public VistaCrmPotentialTenantAccessControlList() {
        grant(PotentialTenantBasic, TenantDTO.class, new PotentialTenantInstanceAccess(), READ);
        grant(PotentialTenantAdvanced, TenantDTO.class, new PotentialTenantInstanceAccess(), READ);
        grant(PotentialTenantFull, TenantDTO.class, new PotentialTenantInstanceAccess(), READ | UPDATE);
        grant(PotentialTenantScreening, TenantDTO.class, new PotentialTenantInstanceAccess(), READ);

        grant(PotentialTenantBasic, LeaseParticipantScreeningTO.class, new PotentialTenantScreeningInstanceAccess(), READ);
        grant(PotentialTenantAdvanced, LeaseParticipantScreeningTO.class, new PotentialTenantScreeningInstanceAccess(), READ);
        grant(PotentialTenantFull, LeaseParticipantScreeningTO.class, new PotentialTenantScreeningInstanceAccess(), ALL);
        grant(PotentialTenantScreening, LeaseParticipantScreeningTO.class, new PotentialTenantScreeningInstanceAccess(), ALL);

//        grant(PotentialTenantBasic, PotentialTenantPortalAccessInformationDTO.class, READ);
//        grant(PotentialTenantAdvanced, PotentialTenantPortalAccessInformationDTO.class, READ);
//        grant(PotentialTenantFull, PotentialTenantPortalAccessInformationDTO.class, READ);
//
        // Actions:
        grant(PotentialTenantBasic, new ActionPermission(PotentialTenantListAction.class));
        grant(PotentialTenantAdvanced, new ActionPermission(PotentialTenantListAction.class));
        grant(PotentialTenantFull, new ActionPermission(PotentialTenantListAction.class));
        grant(PotentialTenantScreening, new ActionPermission(PotentialTenantListAction.class));

//        grant(PotentialTenantBasic, new ActionPermission(PotentialTenantChangePassword.class));
//        grant(PotentialTenantAdvanced, new ActionPermission(PotentialTenantChangePassword.class));
//        grant(PotentialTenantFull, new ActionPermission(PotentialTenantChangePassword.class));
    }
}
