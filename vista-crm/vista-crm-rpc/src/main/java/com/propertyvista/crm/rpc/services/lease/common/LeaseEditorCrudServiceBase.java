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
package com.propertyvista.crm.rpc.services.lease.common;

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractVersionedCrudService;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseEditorCrudServiceBase<DTO extends LeaseDTO> extends AbstractVersionedCrudService<DTO> {

    void setSelectedUnit(AsyncCallback<DTO> callback, AptUnit unitId, DTO currentValue);

    void setSelectedService(AsyncCallback<DTO> callback, ProductItem serviceId, DTO currentValue);

    void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem itemId, DTO currentValue);

    void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, ProductItemType productType, DTO currentValue);

    void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item);
}
