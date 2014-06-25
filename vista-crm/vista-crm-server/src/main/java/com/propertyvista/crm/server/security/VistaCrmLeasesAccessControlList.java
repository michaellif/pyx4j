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

import static com.propertyvista.domain.security.VistaCrmBehavior.LeasesAdvance;
import static com.propertyvista.domain.security.VistaCrmBehavior.LeasesBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.LeasesFull;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseAgreementSigning;
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

public class VistaCrmLeasesAccessControlList extends UIAclBuilder {

    VistaCrmLeasesAccessControlList() {

        //  ---- Actions:
        // TODO move ? to proper section in this file
        grant(LeasesBasic, new ActionPermission(SendMail.class));
        grant(LeasesAdvance, new ActionPermission(SendMail.class));
        grant(LeasesFull, new ActionPermission(SendMail.class));

        grant(LeasesAdvance, new ActionPermission(LeaseAgreementSigning.class));
        grant(LeasesFull, new ActionPermission(LeaseAgreementSigning.class));

        grant(LeasesFull, new ActionPermission(LeaseRunBill.class));

        grant(LeasesFull, new ActionPermission(LeaseStateManagement.class));

        // ---- Lease(Term) itself:
        grant(LeasesBasic, LeaseDTO.class, READ);
        grant(LeasesAdvance, LeaseDTO.class, READ);
        grant(LeasesFull, LeaseDTO.class, ALL);

        grant(LeasesBasic, LeaseTermDTO.class, READ);
        grant(LeasesAdvance, LeaseTermDTO.class, READ);
        grant(LeasesFull, LeaseTermDTO.class, ALL);

        // ---- Legal/Documentation:

        grant(LeasesAdvance, LegalLetter.class, READ);
        grant(LeasesFull, LegalLetter.class, ALL);

        grant(LeasesAdvance, LeaseLegalStateDTO.class, READ);
        grant(LeasesFull, LeaseLegalStateDTO.class, ALL);

        grant(LeasesAdvance, HasNotesAndAttachments.class, READ);
        grant(LeasesFull, HasNotesAndAttachments.class, ALL);

        // ---- Financial:

        grant(LeasesAdvance, LeaseAdjustment.class, READ);
        grant(LeasesFull, LeaseAdjustment.class, READ);

        grant(LeasesAdvance, DepositLifecycleDTO.class, READ);
        grant(LeasesFull, DepositLifecycleDTO.class, READ);

        grant(LeasesAdvance, TransactionHistoryDTO.class, READ);
        grant(LeasesFull, TransactionHistoryDTO.class, READ);

        grant(LeasesAdvance, BillDataDTO.class, READ);
        grant(LeasesFull, BillDataDTO.class, READ);

        // ---- Payment:
        grant(LeasesBasic, PaymentRecordDTO.class, READ);
        grant(LeasesBasic, PreauthorizedPaymentsDTO.class, READ);
        grant(LeasesBasic, AutoPayHistoryDTO.class, READ);
        grant(LeasesBasic, new IServiceExecutePermission(AutoPayHistoryCrudService.class));

        grant(LeasesAdvance, PaymentRecordDTO.class, READ);
        grant(LeasesAdvance, PreauthorizedPaymentsDTO.class, READ);
        grant(LeasesAdvance, AutoPayHistoryDTO.class, READ);
        grant(LeasesAdvance, new IServiceExecutePermission(AutoPayHistoryCrudService.class));

        grant(LeasesFull, PaymentRecordDTO.class, ALL);
        grant(LeasesFull, PreauthorizedPaymentsDTO.class, ALL);
        grant(LeasesFull, AutoPayHistoryDTO.class, READ);
        grant(LeasesFull, new IServiceExecutePermission(AutoPayHistoryCrudService.class));
        //See also VistaCrmFinancialAccessControlList

    }

}
