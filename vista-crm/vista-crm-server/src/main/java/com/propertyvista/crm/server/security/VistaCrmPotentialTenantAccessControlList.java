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
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.PotentialTenantScreening;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.security.PotentialGuarantorInstanceAccess;
import com.propertyvista.crm.rpc.security.PotentialLeaseParticipantScreeningInstanceAccess;
import com.propertyvista.crm.rpc.security.PotentialTenantInstanceAccess;
import com.propertyvista.crm.rpc.services.customer.GuarantorPasswordChangeService;
import com.propertyvista.crm.rpc.services.customer.TenantPasswordChangeService;
import com.propertyvista.crm.rpc.services.customer.ac.GuarantorChangePassword;
import com.propertyvista.crm.rpc.services.customer.ac.PotentialTenantListAction;
import com.propertyvista.crm.rpc.services.customer.ac.TenantChangePassword;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.dto.TenantDTO;

class VistaCrmPotentialTenantAccessControlList extends UIAclBuilder {

    VistaCrmPotentialTenantAccessControlList() {
        grant(PotentialTenantBasic, TenantDTO.class, new PotentialTenantInstanceAccess(), READ);
        grant(PotentialTenantBasic, GuarantorDTO.class, new PotentialGuarantorInstanceAccess(), READ);

        grant(PotentialTenantAdvanced, TenantDTO.class, new PotentialTenantInstanceAccess(), READ);
        grant(PotentialTenantAdvanced, GuarantorDTO.class, new PotentialGuarantorInstanceAccess(), READ);

        grant(PotentialTenantFull, TenantDTO.class, new PotentialTenantInstanceAccess(), READ | UPDATE);
        grant(PotentialTenantFull, GuarantorDTO.class, new PotentialGuarantorInstanceAccess(), READ | UPDATE);

        grant(PotentialTenantScreening, TenantDTO.class, new PotentialTenantInstanceAccess(), READ);
        grant(PotentialTenantScreening, GuarantorDTO.class, new PotentialGuarantorInstanceAccess(), READ);

        grant(PotentialTenantFull, LeaseParticipantScreeningTO.class, new PotentialTenantInstanceAccess(), ALL);
        grant(PotentialTenantFull, LeaseParticipantScreeningTO.class, new PotentialGuarantorInstanceAccess(), ALL);
        grant(PotentialTenantFull, LeaseParticipantScreeningTO.class, new PotentialLeaseParticipantScreeningInstanceAccess(), ALL);

        grant(PotentialTenantScreening, LeaseParticipantScreeningTO.class, new PotentialTenantInstanceAccess(), ALL);
        grant(PotentialTenantScreening, LeaseParticipantScreeningTO.class, new PotentialGuarantorInstanceAccess(), ALL);
        grant(PotentialTenantScreening, LeaseParticipantScreeningTO.class, new PotentialLeaseParticipantScreeningInstanceAccess(), ALL);

        // Actions:
        grant(PotentialTenantBasic, PotentialTenantListAction.class);
        grant(PotentialTenantAdvanced, PotentialTenantListAction.class);
        grant(PotentialTenantFull, PotentialTenantListAction.class);
        grant(PotentialTenantScreening, PotentialTenantListAction.class);

        // change password for potential tenant:
        grant(PotentialTenantBasic, TenantChangePassword.class, new PotentialTenantInstanceAccess());
        grant(PotentialTenantAdvanced, TenantChangePassword.class, new PotentialTenantInstanceAccess());
        grant(PotentialTenantScreening, TenantChangePassword.class, new PotentialTenantInstanceAccess());
        grant(PotentialTenantFull, TenantChangePassword.class, new PotentialTenantInstanceAccess());

        grant(PotentialTenantBasic, PotentialTenantAdvanced, PotentialTenantScreening, new IServiceExecutePermission(TenantPasswordChangeService.class));
        grant(PotentialTenantFull, new IServiceExecutePermission(TenantPasswordChangeService.class));

        // change password for potential guarantor:
        grant(PotentialTenantBasic, GuarantorChangePassword.class, new PotentialGuarantorInstanceAccess());
        grant(PotentialTenantAdvanced, GuarantorChangePassword.class, new PotentialGuarantorInstanceAccess());
        grant(PotentialTenantScreening, GuarantorChangePassword.class, new PotentialGuarantorInstanceAccess());
        grant(PotentialTenantFull, GuarantorChangePassword.class, new PotentialGuarantorInstanceAccess());

        grant(PotentialTenantBasic, PotentialTenantAdvanced, PotentialTenantScreening, new IServiceExecutePermission(GuarantorPasswordChangeService.class));
        grant(PotentialTenantFull, new IServiceExecutePermission(GuarantorPasswordChangeService.class));
    }
}
