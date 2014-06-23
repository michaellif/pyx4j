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

import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialAggregatedTransfer;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialMoneyIN;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialPayments;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;
import com.propertyvista.crm.rpc.services.financial.AutoPayReviewService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchDepositSlipPrintService;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.property.asset.building.BuildingFinancial;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.PaymentRecordDTO;

public class VistaCrmFinancialAccessControlList extends UIAclBuilder {

    VistaCrmFinancialAccessControlList() {

        // ------ Financial: Money IN
        grant(FinancialMoneyIN, MoneyInBatchDTO.class, ALL);
        grant(FinancialMoneyIN, new IServiceExecutePermission(MoneyInToolService.class));
        grant(FinancialMoneyIN, new IServiceExecutePermission(MoneyInBatchCrudService.class));
        grant(FinancialMoneyIN, new IServiceExecutePermission(MoneyInBatchDepositSlipPrintService.class));

        // ------ Financial: Aggregated Transfer 
        grant(FinancialAggregatedTransfer, AggregatedTransfer.class, READ);
        grant(FinancialAggregatedTransfer, new IServiceExecutePermission(AggregatedTransferCrudService.class));

        // ------ Financial: Payments
        grant(FinancialPayments, PaymentRecordDTO.class, ALL);

        grant(FinancialPayments, new IServiceExecutePermission(AutoPayReviewService.class));
        grant(FinancialPayments, PapReviewDTO.class, ALL);

        grant(FinancialPayments, PaymentRecordDTO.class, ALL);
        grant(FinancialPayments, PreauthorizedPaymentsDTO.class, ALL);
        grant(FinancialPayments, AutoPayHistoryDTO.class, READ);
        grant(FinancialPayments, new IServiceExecutePermission(AutoPayHistoryCrudService.class));
        // See also VistaCrmLeasesAccessControlList

        // ------ Financial: Full
        grant(FinancialFull, VistaCrmBehavior.FinancialMoneyIN);
        grant(FinancialFull, VistaCrmBehavior.FinancialAggregatedTransfer);
        grant(FinancialFull, VistaCrmBehavior.FinancialPayments);

        grant(FinancialFull, BuildingFinancial.class, READ);
        grant(FinancialFull, BillingCycleDTO.class, READ);

    }
}
