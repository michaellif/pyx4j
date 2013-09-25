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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleBillListService;
import com.propertyvista.domain.financial.billing.Bill;

public class BillingCycleBillListServiceImpl extends AbstractListServiceDtoImpl<Bill, BillDataDTO> implements BillingCycleBillListService {

    public BillingCycleBillListServiceImpl() {
        super(Bill.class, BillDataDTO.class);
    }

    @Override
    protected void bind() {
        bind(boClass, toProto.bill(), boProto);
    }

    @Override
    public void confirm(AsyncCallback<VoidSerializable> callback, Vector<BillDataDTO> bills) {
        for (BillDataDTO bill : bills) {
            ServerSideFactory.create(BillingFacade.class).confirmBill(bill.bill());
            Persistence.service().commit();
        }
        callback.onSuccess(null);
    }

    @Override
    public void reject(AsyncCallback<VoidSerializable> callback, Vector<BillDataDTO> bills, String reason) {
        for (BillDataDTO bill : bills) {
            ServerSideFactory.create(BillingFacade.class).rejectBill(bill.bill(), reason);
            Persistence.service().commit();
        }
        callback.onSuccess(null);
    }
}