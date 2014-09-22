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

import static com.propertyvista.domain.security.VistaCrmBehavior.GuarantorAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.GuarantorBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.GuarantorFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.security.FormerGuarantorInstanceAccess;
import com.propertyvista.crm.rpc.security.FormerLeaseParticipantScreeningInstanceAccess;
import com.propertyvista.crm.rpc.security.GuarantorInstanceAccess;
import com.propertyvista.crm.rpc.security.LeaseParticipantScreeningInstanceAccess;
import com.propertyvista.crm.rpc.services.customer.GuarantorPasswordChangeService;
import com.propertyvista.crm.rpc.services.customer.ac.FormerGuarantorListAction;
import com.propertyvista.crm.rpc.services.customer.ac.GuarantorChangePassword;
import com.propertyvista.crm.rpc.services.customer.ac.GuarantorListAction;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

class VistaCrmGuarantorAccessControlList extends UIAclBuilder {

    VistaCrmGuarantorAccessControlList() {

        grant(GuarantorBasic, GuarantorDTO.class, new GuarantorInstanceAccess(), READ);
        grant(GuarantorAdvanced, GuarantorDTO.class, new GuarantorInstanceAccess(), READ | UPDATE);
        grant(GuarantorFull, GuarantorDTO.class, new GuarantorInstanceAccess(), READ | UPDATE);

        grant(GuarantorAdvanced, LeaseParticipantScreeningTO.class, new GuarantorInstanceAccess(), READ);
        grant(GuarantorAdvanced, LeaseParticipantScreeningTO.class, new LeaseParticipantScreeningInstanceAccess(), READ);

        grant(GuarantorFull, LeaseParticipantScreeningTO.class, new GuarantorInstanceAccess(), ALL);
        grant(GuarantorFull, LeaseParticipantScreeningTO.class, new LeaseParticipantScreeningInstanceAccess(), ALL);

        // Actions:
        grant(GuarantorBasic, GuarantorListAction.class);
        grant(GuarantorAdvanced, GuarantorListAction.class);
        grant(GuarantorFull, GuarantorListAction.class);

        grant(GuarantorBasic, GuarantorChangePassword.class, new GuarantorInstanceAccess());
        grant(GuarantorAdvanced, GuarantorChangePassword.class, new GuarantorInstanceAccess());
        grant(GuarantorFull, GuarantorChangePassword.class, new GuarantorInstanceAccess());
        grant(GuarantorBasic, GuarantorAdvanced, GuarantorFull, new IServiceExecutePermission(GuarantorPasswordChangeService.class));

        // --------------------------------------------------------------------------------------------------------------------

        // Former Guarantors:
        grant(GuarantorAdvanced, GuarantorDTO.class, new FormerGuarantorInstanceAccess(), READ);
        grant(GuarantorFull, GuarantorDTO.class, new FormerGuarantorInstanceAccess(), READ | UPDATE);

        grant(GuarantorAdvanced, LeaseParticipantScreeningTO.class, new FormerGuarantorInstanceAccess(), READ);
        grant(GuarantorAdvanced, LeaseParticipantScreeningTO.class, new FormerLeaseParticipantScreeningInstanceAccess(), READ);

        grant(GuarantorFull, LeaseParticipantScreeningTO.class, new FormerGuarantorInstanceAccess(), READ | UPDATE);
        grant(GuarantorFull, LeaseParticipantScreeningTO.class, new FormerLeaseParticipantScreeningInstanceAccess(), READ | UPDATE);

        // Actions:
        grant(GuarantorAdvanced, FormerGuarantorListAction.class);
        grant(GuarantorFull, FormerGuarantorListAction.class);
    }
}
