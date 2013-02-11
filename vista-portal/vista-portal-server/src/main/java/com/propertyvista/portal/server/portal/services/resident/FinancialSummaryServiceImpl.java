/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.portal.domain.dto.FinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.services.resident.FinancialSummaryService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.shared.config.VistaFeatures;

public class FinancialSummaryServiceImpl implements FinancialSummaryService {

    @Override
    public void getFinancialStatus(AsyncCallback<FinancialSummaryDTO> callback) {
        if (!VistaFeatures.instance().yardiIntegration()) {
            throw new IllegalStateException("this PMC should be integrated with Yardi");
        }
        EntityQueryCriteria<YardiBillingAccount> criteria = EntityQueryCriteria.create(YardiBillingAccount.class);
        criteria.eq(criteria.proto().lease(), TenantAppContext.getCurrentUserTenant().lease());
        YardiBillingAccount yardiBillingAccount = Persistence.service().retrieve(criteria);

        if (yardiBillingAccount != null) {
            FinancialSummaryDTO status = EntityFactory.create(FinancialSummaryDTO.class);
            // TODO set balance... 
            status.currentBalance();
            status.transactionsHistory().set(ServerSideFactory.create(ARFacade.class).getTransactionHistory(yardiBillingAccount));
            callback.onSuccess(status);
        } else {
            throw new Error("Yardi Billng Account for tenant " + TenantAppContext.getCurrentUserTenant().getPrimaryKey() + " was not found");
        }

    }
}
