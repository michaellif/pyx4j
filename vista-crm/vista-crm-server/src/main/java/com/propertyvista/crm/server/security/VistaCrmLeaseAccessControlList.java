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

import static com.propertyvista.domain.security.VistaCrmBehavior.LeaseAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.LeaseBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.LeaseFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.security.LeaseTermEditOnLeaseInstanceAccess;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.lease.ac.FormerLeaseListAction;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseAgreementSigning;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseConfirmBill;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseRenew;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseReserveUnit;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseRunBill;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseStateManagement;
import com.propertyvista.crm.rpc.services.lease.ac.SendMail;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LeaseLegalStateDTO;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.dto.TransactionHistoryDTO;

class VistaCrmLeaseAccessControlList extends UIAclBuilder {

    VistaCrmLeaseAccessControlList() {

        //  ---- Actions:
        // TODO move ? to proper section in this file
        grant(LeaseBasic, SendMail.class);
        grant(LeaseAdvanced, SendMail.class);
        grant(LeaseFull, SendMail.class);

        grant(LeaseAdvanced, LeaseAgreementSigning.class);
        grant(LeaseFull, LeaseAgreementSigning.class);

        grant(LeaseFull, LeaseRunBill.class);
        grant(LeaseFull, LeaseConfirmBill.class);

        grant(LeaseFull, LeaseReserveUnit.class);
        grant(LeaseFull, LeaseStateManagement.class);
        grant(LeaseFull, LeaseRenew.class);

        // access to former leases accordion menu
        grant(LeaseAdvanced, FormerLeaseListAction.class);
        grant(LeaseFull, FormerLeaseListAction.class);

        {// ---- Lease(Term) itself:
            List<Class<? extends IEntity>> entities = entities(LeaseDTO.class, LeaseTermDTO.class);
            grant(LeaseBasic, entities, READ);

            grant(LeaseAdvanced, entities, READ);

            grant(LeaseFull, LeaseDTO.class, ALL);
            grant(LeaseFull, LeaseTermDTO.class, new LeaseTermEditOnLeaseInstanceAccess(), ALL);
        }

        // ---- Legal/Documentation:
        // See also VistaCrmLegalAccessControlList

        grant(LeaseAdvanced, LegalLetter.class, READ);
        grant(LeaseAdvanced, LeaseLegalStateDTO.class, READ);

        grant(LeaseFull, LegalLetter.class, ALL);
        grant(LeaseFull, LeaseLegalStateDTO.class, ALL);

        // ---- Financial:
        // See also VistaCrmFinancialAccessControlList

        grant(LeaseAdvanced, LeaseAdjustment.class, READ);
        grant(LeaseAdvanced, DepositLifecycleDTO.class, READ);
        grant(LeaseAdvanced, TransactionHistoryDTO.class, READ);

        grant(LeaseFull, LeaseAdjustment.class, READ);
        grant(LeaseFull, DepositLifecycleDTO.class, READ);
        grant(LeaseFull, TransactionHistoryDTO.class, READ);

        // Bills
        grant(LeaseAdvanced, BillDataDTO.class, READ);
        grant(LeaseAdvanced, new IServiceExecutePermission(BillCrudService.class));

        grant(LeaseFull, BillDataDTO.class, READ);
        grant(LeaseFull, new IServiceExecutePermission(BillCrudService.class));
    }
}
