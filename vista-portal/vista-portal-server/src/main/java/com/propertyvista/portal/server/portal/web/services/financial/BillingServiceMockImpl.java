/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.financial;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillViewDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.LatestActivitiesDTO;
import com.propertyvista.portal.rpc.portal.web.services.financial.BillingService;

public class BillingServiceMockImpl implements BillingService {

    @Override
    public void retreiveBillingSummary(AsyncCallback<BillingSummaryDTO> callback) {

        BillingSummaryDTO billingSummary = EntityFactory.create(BillingSummaryDTO.class);

        billingSummary.currentBalance().setValue(new BigDecimal("1300.00"));
        billingSummary.dueDate().setValue(new LogicalDate(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000));

        callback.onSuccess(billingSummary);
    }

    @Override
    public void retreiveBillingHistory(AsyncCallback<BillingHistoryDTO> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retreiveTransactionHistory(AsyncCallback<TransactionHistoryDTO> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retreiveLatestActivities(AsyncCallback<LatestActivitiesDTO> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retreiveBill(AsyncCallback<BillViewDTO> callback, Bill entityId) {
        // TODO Auto-generated method stub

    }

}
