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

import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;
import com.propertyvista.crm.rpc.services.lease.ac.SendMail;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public class VistaCrmLeasesAccessControlList extends UIAclBuilder {

    VistaCrmLeasesAccessControlList() {

        // TODO move ? to proper secion in this file
        grant(LeasesBasic, new ActionPermission(SendMail.class));
        grant(LeasesAdvance, new ActionPermission(SendMail.class));
        grant(LeasesFull, new ActionPermission(SendMail.class));

        //------ Lease itself:
        grant(LeasesBasic, LeaseDTO.class, READ);
        grant(LeasesAdvance, LeaseDTO.class, READ);
        grant(LeasesFull, LeaseDTO.class, ALL);

        //------ Payment:
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
