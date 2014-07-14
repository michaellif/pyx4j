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
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseAgreementSigning;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseConfirmBill;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseRunBill;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseStateManagement;
import com.propertyvista.crm.rpc.services.lease.ac.SendMail;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LeaseLegalStateDTO;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.TransactionHistoryDTO;

public class VistaCrmLeaseAccessControlList extends UIAclBuilder {

    VistaCrmLeaseAccessControlList() {

        //  ---- Actions:
        // TODO move ? to proper section in this file
        grant(LeaseBasic, new ActionPermission(SendMail.class));
        grant(LeaseAdvanced, new ActionPermission(SendMail.class));
        grant(LeaseFull, new ActionPermission(SendMail.class));

        grant(LeaseAdvanced, new ActionPermission(LeaseAgreementSigning.class));
        grant(LeaseFull, new ActionPermission(LeaseAgreementSigning.class));

        grant(LeaseFull, new ActionPermission(LeaseRunBill.class));
        grant(LeaseFull, new ActionPermission(LeaseConfirmBill.class));

        grant(LeaseFull, new ActionPermission(LeaseStateManagement.class));

        {// ---- Lease(Term) itself:
            List<Class<? extends IEntity>> entities = entities(LeaseDTO.class, LeaseTermDTO.class);
            grant(LeaseBasic, entities, READ);
            grant(LeaseAdvanced, entities, READ);
            grant(LeaseFull, entities, ALL);
        }

        // ---- Legal/Documentation:

        grant(LeaseAdvanced, LegalLetter.class, READ);
        grant(LeaseFull, LegalLetter.class, ALL);

        grant(LeaseAdvanced, LeaseLegalStateDTO.class, READ);
        grant(LeaseFull, LeaseLegalStateDTO.class, ALL);

        // TODO  this BAD  change this
        {
            grant(LeaseAdvanced, HasNotesAndAttachments.class, READ);
            grant(LeaseFull, HasNotesAndAttachments.class, ALL);
        }

        // ---- Financial:

        grant(LeaseAdvanced, LeaseAdjustment.class, READ);
        grant(LeaseFull, LeaseAdjustment.class, READ);

        grant(LeaseAdvanced, DepositLifecycleDTO.class, READ);
        grant(LeaseFull, DepositLifecycleDTO.class, READ);

        grant(LeaseAdvanced, TransactionHistoryDTO.class, READ);
        grant(LeaseFull, TransactionHistoryDTO.class, READ);

        grant(LeaseAdvanced, BillDataDTO.class, READ);
        grant(LeaseFull, BillDataDTO.class, READ | UPDATE);

        // ---- Payment:
        grant(LeaseBasic, PaymentRecordDTO.class, READ);
        grant(LeaseBasic, PreauthorizedPaymentsDTO.class, READ);
        grant(LeaseBasic, AutoPayHistoryDTO.class, READ);
        grant(LeaseBasic, new IServiceExecutePermission(AutoPayHistoryCrudService.class));

        grant(LeaseAdvanced, PaymentRecordDTO.class, READ);
        grant(LeaseAdvanced, PreauthorizedPaymentsDTO.class, READ);
        grant(LeaseAdvanced, AutoPayHistoryDTO.class, READ);
        grant(LeaseAdvanced, new IServiceExecutePermission(AutoPayHistoryCrudService.class));

        grant(LeaseFull, PaymentRecordDTO.class, ALL);
        grant(LeaseFull, PreauthorizedPaymentsDTO.class, ALL);
        grant(LeaseFull, AutoPayHistoryDTO.class, READ);
        grant(LeaseFull, new IServiceExecutePermission(AutoPayHistoryCrudService.class));
        //See also VistaCrmFinancialAccessControlList

    }

}
