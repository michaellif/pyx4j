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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
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

        Lease contextLease = TenantAppContext.getCurrentUserTenant().lease();
        EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
        criteria.eq(criteria.proto().lease(), contextLease);
        BillingAccount contextBillingAccount = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);

        ARFacade arFacade = ServerSideFactory.create(ARFacade.class);
        financialSummary.currentBalance().setValue(arFacade.getCurrentBalance(contextBillingAccount));

        // TODO has to stay here until billing facade and AR facade merged together
        if (financialSummary.isInstanceOf(YardiFinancialSummaryDTO.class)) {
            ((YardiFinancialSummaryDTO) financialSummary).transactionsHistory().set(arFacade.getTransactionHistory(contextBillingAccount));
        } else if (financialSummary.isInstanceOf(PvBillingFinancialSummaryDTO.class)) {
            ((PvBillingFinancialSummaryDTO) financialSummary).currentBill().set(ServerSideFactory.create(BillingFacade.class).getLatestBill(contextLease));
            ((PvBillingFinancialSummaryDTO) financialSummary).latestActivities().addAll(arFacade.getNotAcquiredLineItems(contextBillingAccount));
        }

        return financialSummary;

    }
}
