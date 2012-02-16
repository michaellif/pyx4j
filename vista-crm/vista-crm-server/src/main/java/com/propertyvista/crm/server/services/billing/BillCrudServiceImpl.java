/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;

public class BillCrudServiceImpl extends AbstractCrudServiceImpl<Bill> implements BillCrudService {

    public BillCrudServiceImpl() {
        super(Bill.class);
    }

    @Override
    protected void enhanceRetrieved(Bill entity) {
        // load detached entities:
        Persistence.service().retrieve(entity.charges());
    }

    @Override
    protected void persist(Bill entity) {
        throw new IllegalArgumentException();
    }

    @Override
    public void confirm(AsyncCallback<Bill> callback, Key entityId) {
        Bill bill = Persistence.service().retrieve(Bill.class, entityId);
        if (bill != null) {
            if (bill.billStatus().getValue() == BillStatus.Confirmed) {
                callback.onFailure(new UserRuntimeException("The Bill is confirmed already!"));
            }
            bill.billStatus().setValue(BillStatus.Confirmed);
            Persistence.service().merge(bill);
            callback.onSuccess(bill);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void reject(AsyncCallback<Bill> callback, Key entityId, String reason) {
        Bill bill = Persistence.service().retrieve(Bill.class, entityId);
        if (bill != null) {
            if (bill.billStatus().getValue() == BillStatus.Rejected) {
                callback.onFailure(new UserRuntimeException("The Bill is rejected already!"));
            }
            bill.billStatus().setValue(BillStatus.Rejected);
            bill.rejectReason().setValue(reason);
            Persistence.service().merge(bill);
            callback.onSuccess(bill);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
