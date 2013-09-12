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
package com.propertyvista.portal.rpc.portal.web.services_new.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingHistoryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.LatestActivitiesDTO;

public interface BillingService extends IService {

    void retreiveBillingSummary(AsyncCallback<BillingSummaryDTO> callback);

    void retreiveBillingHistory(AsyncCallback<BillingHistoryDTO> callback);

    void retreiveTransactionHistory(AsyncCallback<TransactionHistoryDTO> callback);

    void retreiveLatestActivities(AsyncCallback<LatestActivitiesDTO> callback);
}
