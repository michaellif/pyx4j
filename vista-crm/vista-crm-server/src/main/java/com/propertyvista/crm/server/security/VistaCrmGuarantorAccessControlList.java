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

import com.propertyvista.crm.rpc.services.customer.GuarantorPasswordChangeService;
import com.propertyvista.crm.rpc.services.customer.ac.FormerGuarantorListAction;
import com.propertyvista.crm.rpc.services.customer.ac.GuarantorChangePassword;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

class VistaCrmGuarantorAccessControlList extends UIAclBuilder {

    VistaCrmGuarantorAccessControlList() {

        grant(GuarantorBasic, GuarantorDTO.class, READ);
        grant(GuarantorAdvanced, GuarantorDTO.class, READ | UPDATE);
        grant(GuarantorFull, GuarantorDTO.class, READ | UPDATE);

        grant(GuarantorAdvanced, LeaseParticipantScreeningTO.class, READ);
        grant(GuarantorFull, LeaseParticipantScreeningTO.class, ALL);

        // Actions:

        // access to former guarantors accordion menu
        grant(GuarantorAdvanced, FormerGuarantorListAction.class);
        grant(GuarantorFull, FormerGuarantorListAction.class);

        grant(GuarantorBasic, GuarantorChangePassword.class);
        grant(GuarantorAdvanced, GuarantorChangePassword.class);
        grant(GuarantorFull, GuarantorChangePassword.class);
        grant(GuarantorBasic, GuarantorAdvanced, GuarantorFull, new IServiceExecutePermission(GuarantorPasswordChangeService.class));
    }
}
