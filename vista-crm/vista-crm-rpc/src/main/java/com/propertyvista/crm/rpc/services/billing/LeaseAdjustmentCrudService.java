/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 */
package com.propertyvista.crm.rpc.services.billing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public interface LeaseAdjustmentCrudService extends AbstractCrudService<LeaseAdjustment> {

    void calculateTax(AsyncCallback<LeaseAdjustment> callback, LeaseAdjustment currentValue);

    void submitAdjustment(AsyncCallback<LeaseAdjustment> callback, Key entityId);
}
