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
 */
package com.propertyvista.crm.server.security;

import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialAdvanced;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialAggregatedTransfer;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialBasic;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialFull;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialMoneyIN;
import static com.propertyvista.domain.security.VistaCrmBehavior.FinancialPayments;
import static com.pyx4j.entity.security.AbstractCRUDPermission.ALL;
import static com.pyx4j.entity.security.AbstractCRUDPermission.READ;
import static com.pyx4j.entity.security.AbstractCRUDPermission.UPDATE;

import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.server.UIAclBuilder;
import com.pyx4j.security.shared.ActionPermission;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.dto.financial.CardsAggregatedTransferDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillPreviewService;
import com.propertyvista.crm.rpc.services.billing.BillPrintService;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.crm.rpc.services.billing.BillingExecutionService;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferCrudService;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferDownloadService;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;
import com.propertyvista.crm.rpc.services.financial.AutoPayReviewService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchCrudService;
import com.propertyvista.crm.rpc.services.financial.MoneyInBatchDepositSlipPrintService;
import com.propertyvista.crm.rpc.services.financial.MoneyInToolService;
import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.crm.rpc.services.financial.RevealAccountNumberService;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseConfirmBill;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseRunBill;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.property.asset.building.BuildingFinancial;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TransactionHistoryDTO;

class VistaCrmFinancialAccessControlList extends UIAclBuilder {

    VistaCrmFinancialAccessControlList() {

        //  ---- Actions:
        grant(FinancialFull, new ActionPermission(LeaseRunBill.class));
        grant(FinancialFull, new ActionPermission(LeaseConfirmBill.class));
        grant(FinancialFull, new IServiceExecutePermission(BillingExecutionService.class));

        // ------ Financial: Money IN
        grant(FinancialMoneyIN, MoneyInBatchDTO.class, ALL);
        grant(FinancialMoneyIN, new IServiceExecutePermission(MoneyInToolService.class));
        grant(FinancialMoneyIN, new IServiceExecutePermission(MoneyInBatchCrudService.class));
        grant(FinancialMoneyIN, new IServiceExecutePermission(MoneyInBatchDepositSlipPrintService.class));

        // ------ Financial: Aggregated Transfer
        grant(FinancialAggregatedTransfer, PreauthorizedPaymentDTO.class, READ);
        grant(FinancialAggregatedTransfer, PreauthorizedPaymentsDTO.class, READ);

        grant(FinancialAggregatedTransfer, AggregatedTransfer.class, READ);
        grant(FinancialAggregatedTransfer, EftAggregatedTransfer.class, READ);
        grant(FinancialAggregatedTransfer, CardsAggregatedTransferDTO.class, READ);
        grant(FinancialAggregatedTransfer, new IServiceExecutePermission(AggregatedTransferCrudService.class));
        grant(FinancialAggregatedTransfer, new IServiceExecutePermission(AggregatedTransferDownloadService.class));

        grant(FinancialAggregatedTransfer, PaymentRecordDTO.class, READ);
        grant(FinancialAggregatedTransfer, new IServiceExecutePermission(PaymentRecordListService.class));

        // ------ Financial: Basic

        grant(FinancialBasic, PreauthorizedPaymentDTO.class, READ);
        grant(FinancialBasic, PreauthorizedPaymentsDTO.class, READ);

        grant(FinancialBasic, PaymentRecordDTO.class, READ);
        grant(FinancialBasic, new IServiceExecutePermission(PaymentRecordListService.class));

        // ------ Financial: Payments

        grant(FinancialPayments, LeasePaymentMethod.class, READ);

        grant(FinancialPayments, PreauthorizedPaymentDTO.class, READ | UPDATE);
        grant(FinancialPayments, PreauthorizedPaymentsDTO.class, READ | UPDATE);

        grant(FinancialPayments, PapReviewDTO.class, READ | UPDATE);
        grant(FinancialPayments, new IServiceExecutePermission(AutoPayReviewService.class));

        grant(FinancialPayments, PaymentRecordDTO.class, ALL);
        grant(FinancialPayments, new IServiceExecutePermission(PaymentRecordListService.class));

        grant(FinancialPayments, TransactionHistoryDTO.class, READ);
        grant(FinancialPayments, AutoPayHistoryDTO.class, READ);
        grant(FinancialPayments, new IServiceExecutePermission(AutoPayHistoryCrudService.class));

        grant(FinancialPayments, new IServiceExecutePermission(RevealAccountNumberService.class));

        grant(FinancialPayments, LeaseAdjustment.class, READ | UPDATE);
        grant(FinancialPayments, DepositLifecycleDTO.class, READ | UPDATE);

        grant(FinancialPayments, TenantFinancialDTO.class, READ);

        // ------ Financial: Advanced

        grant(FinancialAdvanced, FinancialPayments);

        grant(FinancialAdvanced, LeasePaymentMethod.class, ALL);

        grant(FinancialAdvanced, PreauthorizedPaymentDTO.class, ALL);
        grant(FinancialAdvanced, PreauthorizedPaymentsDTO.class, ALL);

        // ------ Financial: Full
        grant(FinancialFull, FinancialMoneyIN);
        grant(FinancialFull, FinancialAggregatedTransfer);
        grant(FinancialFull, FinancialAdvanced);

        grant(FinancialFull, BuildingFinancial.class, READ);
        grant(FinancialFull, BillingCycleDTO.class, READ);
        grant(FinancialFull, new IServiceExecutePermission(BillingCycleCrudService.class));

        grant(FinancialFull, BillDTO.class, READ);
        grant(FinancialFull, BillDataDTO.class, READ);
        grant(FinancialFull, new IServiceExecutePermission(BillCrudService.class));
        grant(FinancialFull, new IServiceExecutePermission(BillPreviewService.class));
        grant(FinancialFull, new IServiceExecutePermission(BillPrintService.class));

        grant(FinancialFull, BuildingMerchantAccount.class, READ);

        grant(FinancialFull, LeaseAdjustment.class, ALL);
        grant(FinancialFull, DepositLifecycleDTO.class, ALL);

        grant(FinancialFull, TenantFinancialDTO.class, READ);
    }
}
