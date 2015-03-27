/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-27
 * @author Vlad
 */
package com.propertyvista.crm.rpc.services.billing;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.annotations.AccessControl;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseConfirmBill;

public interface BillingCycleBillListService extends AbstractListCrudService<BillDataDTO> {

    @AccessControl(LeaseConfirmBill.class)
    void confirm(AsyncCallback<VoidSerializable> callback, Vector<BillDataDTO> bills);

    @AccessControl(LeaseConfirmBill.class)
    void reject(AsyncCallback<VoidSerializable> callback, Vector<BillDataDTO> bills, String reason);
}
