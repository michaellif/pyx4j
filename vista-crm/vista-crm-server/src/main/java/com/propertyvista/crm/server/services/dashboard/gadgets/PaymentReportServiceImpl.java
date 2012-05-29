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
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentReportServiceImpl implements PaymentReportService {

    @Override
    public void paymentRecords(AsyncCallback<EntitySearchResult<PaymentRecord>> callback, Vector<Building> buildings, LogicalDate targetDate,
            PaymentType paymentTypeCriteria, Vector<PaymentStatus> paymentStatusCriteria) {

        // TODO convert to method arguments
        int pageSize = 10;
        int pageNumber = 1;

        EntityListCriteria<PaymentRecord> criteria = EntityListCriteria.create(PaymentRecord.class);

        criteria.setPageSize(pageSize);
        criteria.setPageNumber(0);

        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().billingAccount().lease().unit().belongsTo(), buildings));
        }
//        criteria.add(PropertyCriterion.ge(criteria.proto().targetDate(), targetDate));
        if (paymentTypeCriteria != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), paymentTypeCriteria));
        }
        if (paymentStatusCriteria != null & !paymentStatusCriteria.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().paymentStatus(), paymentStatusCriteria));
        }

        Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>();
        ICursorIterator<PaymentRecord> i = Persistence.service().query(null, criteria, AttachLevel.Attached);

        while (i.hasNext()) {
            PaymentRecord paymentRecord = i.next();

            Persistence.service().retrieve(paymentRecord.billingAccount());
            Persistence.service().retrieve(paymentRecord.billingAccount().lease());
            Persistence.service().retrieve(paymentRecord.billingAccount().lease().version().tenants());
            Persistence.service().retrieve(paymentRecord.billingAccount().lease().unit());
            Persistence.service().retrieve(paymentRecord.billingAccount().lease().unit().belongsTo());

            paymentRecords.add(paymentRecord);
        }

        EntitySearchResult<PaymentRecord> result = new EntitySearchResult<PaymentRecord>();
        result.setData(paymentRecords);
        result.setTotalRows(Persistence.service().count(criteria));

        // TODO
        result.hasMoreData(true);

        callback.onSuccess(result);
    }
}
