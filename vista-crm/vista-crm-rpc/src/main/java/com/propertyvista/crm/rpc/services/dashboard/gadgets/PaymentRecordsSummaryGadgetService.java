/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesHolderDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.property.asset.building.Building;

public interface PaymentRecordsSummaryGadgetService extends IService {

    void paymentRecordsSummary(AsyncCallback<EntitySearchResult<PaymentsSummary>> callback, Vector<Building> buildings, LogicalDate targetDate,
            Vector<PaymentRecord.PaymentStatus> paymentStatusCriteria, int pageNumber, int pageSize, Vector<Sort> sortingCriteria);

    void fundsTransferFees(AsyncCallback<PaymentFeesHolderDTO> callback);

}
