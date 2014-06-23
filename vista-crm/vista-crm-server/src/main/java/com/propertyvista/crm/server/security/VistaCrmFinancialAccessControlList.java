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

import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchDepositSlipPrintService;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class VistaCrmFinancialAccessControlList extends UIAclBuilder {

    VistaCrmFinancialAccessControlList() {

        // ------ Financial: Money IN
        grant(VistaCrmBehavior.FinancialMoneyIN, MoneyInBatchDTO.class, ALL);
        grant(VistaCrmBehavior.FinancialMoneyIN, new IServiceExecutePermission(MoneyInToolService.class));
        grant(VistaCrmBehavior.FinancialMoneyIN, new IServiceExecutePermission(MoneyInBatchCrudService.class));
        grant(VistaCrmBehavior.FinancialMoneyIN, new IServiceExecutePermission(MoneyInBatchDepositSlipPrintService.class));

        // ------ Financial: Aggregated Transfer 
        grant(VistaCrmBehavior.FinancialAggregatedTransfer, AggregatedTransfer.class, READ);
        grant(VistaCrmBehavior.FinancialAggregatedTransfer, new IServiceExecutePermission(AggregatedTransferCrudService.class));
    }
}
