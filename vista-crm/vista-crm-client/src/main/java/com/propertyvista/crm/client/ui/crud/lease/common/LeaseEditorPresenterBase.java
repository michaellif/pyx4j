/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;

public interface LeaseEditorPresenterBase {

    void setSelectedUnit(AptUnit item);

    void setSelectedService(ProductItem item);

    void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem item);

    void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, ProductItem item);

    void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item);
}
