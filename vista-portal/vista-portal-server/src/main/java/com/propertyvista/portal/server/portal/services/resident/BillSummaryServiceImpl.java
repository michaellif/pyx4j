/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-31
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
import com.propertyvista.portal.domain.dto.financial.PaymentInfoDTO;
import com.propertyvista.portal.domain.dto.financial.PvBillingFinancialSummaryDTO;
import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.services.resident.BillSummaryService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.shared.config.VistaFeatures;

public class BillSummaryServiceImpl implements BillSummaryService {

    @Override
    public void retrieve(AsyncCallback<FinancialSummaryDTO> callback) {
        callback.onSuccess(retrieve());
    }

    // TODO this method should be more polymophic (with less "IF"s and "instance off"s), the question is how?
    static FinancialSummaryDTO retrieve() {
        FinancialSummaryDTO financialSummary = VistaFeatures.instance().yardiIntegration() ? EntityFactory.create(YardiFinancialSummaryDTO.class)
                : EntityFactory.create(PvBillingFinancialSummaryDTO.class);

        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserTenant().lease().getPrimaryKey());

        financialSummary.currentBalance().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));

        LogicalDate excutionDate = ServerSideFactory.create(PaymentMethodFacade.class).getCurrentPreauthorizedPaymentDate(lease);
        for (PreauthorizedPayment pap : ServerSideFactory.create(PaymentMethodFacade.class).retrieveCurrentPreauthorizedPayments(lease)) {
            PaymentInfoDTO pi = EntityFactory.create(PaymentInfoDTO.class);

            pi.amount().setValue(BigDecimal.ZERO);
            for (PreauthorizedPaymentCoveredItem ci : pap.coveredItems()) {
                pi.amount().setValue(pi.amount().getValue().add(ci.amount().getValue()));
            }

            pi.paymentMethod().set(pap.paymentMethod());
            pi.paymentDate().setValue(excutionDate);
            pi.payer().set(pap.tenant());
            Persistence.ensureRetrieve(pi.payer(), AttachLevel.ToStringMembers);

            financialSummary.currentAutoPayments().add(pi);
        }

        // TODO has to stay here until billing facade and AR facade merged together
        if (financialSummary.isInstanceOf(YardiFinancialSummaryDTO.class)) {
            ((YardiFinancialSummaryDTO) financialSummary).transactionsHistory().set(
                    ServerSideFactory.create(ARFacade.class).getTransactionHistory(lease.billingAccount()));
            ((YardiFinancialSummaryDTO) financialSummary).latestActivities().addAll(
                    ServerSideFactory.create(ARFacade.class).getLatestBillingActivity(lease.billingAccount()));
        } else if (financialSummary.isInstanceOf(PvBillingFinancialSummaryDTO.class)) {
            ((PvBillingFinancialSummaryDTO) financialSummary).currentBill().set(ServerSideFactory.create(BillingFacade.class).getLatestBill(lease));
            ((PvBillingFinancialSummaryDTO) financialSummary).latestActivities().addAll(
                    ServerSideFactory.create(ARFacade.class).getLatestBillingActivity(lease.billingAccount()));
        }

        return financialSummary;

    }
}
