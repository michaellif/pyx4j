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

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.services.customer.CustomerCreditCheckLongReportService;
import com.propertyvista.crm.rpc.services.lease.ac.CreditCheckRun;
import com.propertyvista.crm.rpc.services.lease.ac.CreditCheckViewReport;
import com.propertyvista.crm.rpc.services.vista2pmc.CreditCheckStatusService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;

public class VistaCrmCreditCheckAccessControlList extends UIAclBuilder {

    VistaCrmCreditCheckAccessControlList() {

        grant(VistaBasicBehavior.CRM, new IServiceExecutePermission(CreditCheckStatusService.class));

        grant(VistaCrmBehavior.CreditCheckBasic, new ActionPermission(CreditCheckRun.class));
        grant(VistaCrmBehavior.CreditCheckFull, new ActionPermission(CreditCheckRun.class));

        grant(VistaCrmBehavior.CreditCheckFull, new ActionPermission(CreditCheckViewReport.class));
        grant(VistaCrmBehavior.CreditCheckFull, new IServiceExecutePermission(CustomerCreditCheckLongReportService.class));
    }

}
